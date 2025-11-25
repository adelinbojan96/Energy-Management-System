package com.example.demo.services;

import com.example.demo.dtos.HourlyConsumptionDTO;
import com.example.demo.dtos.MeasurementDTO;
import com.example.demo.dtos.SyncEventDTO;
import com.example.demo.entities.DeviceUserMapping;
import com.example.demo.entities.HourlyConsumption;
import com.example.demo.repositories.DeviceUserMappingRepository;
import com.example.demo.repositories.HourlyConsumptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MonitoringService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MonitoringService.class);

    private final HourlyConsumptionRepository consumptionRepo;
    private final DeviceUserMappingRepository mappingRepo;

    public MonitoringService(HourlyConsumptionRepository consumptionRepo, DeviceUserMappingRepository mappingRepo) {
        this.consumptionRepo = consumptionRepo;
        this.mappingRepo = mappingRepo;
    }

    @RabbitListener(queues = "${rabbitmq.queue.data}")
    @Transactional
    public void consumeMeasurement(MeasurementDTO message) {
        LocalDateTime actualTimestamp = message.getLocalDateTime();

        if (actualTimestamp == null || message.getDeviceId() == null || message.getValue() == null) {
            LOGGER.error("Received invalid measurement message: {}", message);
            return;
        }

        LOGGER.debug("Received measurement for device {}: {}", message.getDeviceId(), message.getValue());

        LocalDateTime truncatedTimestamp = actualTimestamp.truncatedTo(ChronoUnit.HOURS);
        UUID deviceId = message.getDeviceId();

        Optional<HourlyConsumption> existingRecord = consumptionRepo.findByDeviceIdAndTimestamp(deviceId, truncatedTimestamp);

        if (existingRecord.isPresent()) {
            HourlyConsumption record = existingRecord.get();
            record.setTotalConsumption(record.getTotalConsumption() + message.getValue());
            consumptionRepo.save(record);
            LOGGER.info("Updated consumption for device {}. Hour: {}, New Total: {}", deviceId, truncatedTimestamp, record.getTotalConsumption());
            
        } else {
            HourlyConsumption newRecord = new HourlyConsumption(deviceId, truncatedTimestamp, message.getValue());
            consumptionRepo.save(newRecord);
            LOGGER.info("Created new consumption record for device {}. Hour: {}", deviceId, truncatedTimestamp);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.sync.monitoring}")
    public void consumeSyncEvent(SyncEventDTO event) {
        LOGGER.info("Received sync event payload: {}", event);

        if (event.getDeviceId() == null) {
            LOGGER.warn("Received sync event with null Device ID. Dropping.");
            return;
        }

        if (event.getEventType() == null) {
             LOGGER.warn("Received sync event with null Event Type. Dropping.");
             return;
        }

        switch (event.getEventType()) {
            case "DEVICE_CREATED":
            case "DEVICE_MAPPED": 
            case "DEVICE_UPDATED":
                handleUpsertDevice(event);
                break;

            case "DEVICE_DELETED":
                handleDeleteDevice(event.getDeviceId());
                break;

            default:
                LOGGER.debug("Ignored event type: {}", event.getEventType());
                break;
        }
    }

    private void handleUpsertDevice(SyncEventDTO event) {
        if (event.getUserId() == null) {
            LOGGER.warn("Cannot map device {} without a User ID", event.getDeviceId());
            return;
        }
        
        try {
        } catch (Exception e) {
        }

        DeviceUserMapping mapping = new DeviceUserMapping();
        mapping.setDeviceId(event.getDeviceId());
        mapping.setUserId(event.getUserId());
        
        if (event.getMaxConsumption() != null) {
             mapping.setMaxConsumption(event.getMaxConsumption());
        }

        try {
            mappingRepo.save(mapping);
            LOGGER.info("Synced mapping for Device {} to User {}", event.getDeviceId(), event.getUserId());
        } catch (DataIntegrityViolationException e) {
            LOGGER.warn("Mapping already exists for Device {} and User {}. Skipping insertion to avoid duplicate.", event.getDeviceId(), event.getUserId());
        } catch (Exception e) {
            LOGGER.error("Failed to save device mapping: {}", e.getMessage());
        }
    }

    private void handleDeleteDevice(UUID deviceId) {
        LOGGER.info("Processed DELETE sync for device {}", deviceId);
        try {
            mappingRepo.deleteByDeviceId(deviceId);
        } catch (Exception e) {
            LOGGER.warn("Could not delete mapping for device {}: {}", deviceId, e.getMessage());
        }
    }

    public List<HourlyConsumptionDTO> getChartDataForUser(UUID userId, LocalDate date) {
        List<DeviceUserMapping> mappings = mappingRepo.findByUserId(userId);
        if (mappings.isEmpty()) {
            return List.of();
        }

        List<UUID> deviceIds = mappings.stream()
                .map(DeviceUserMapping::getDeviceId)
                .collect(Collectors.toList());

        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        List<HourlyConsumption> consumptions = consumptionRepo.findByDeviceIdInAndTimestampBetween(deviceIds, startOfDay, endOfDay);
        
        return consumptions.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getTimestamp().getHour(),
                        Collectors.summingDouble(HourlyConsumption::getTotalConsumption)
                ))
                .entrySet().stream()
                .map(entry -> new HourlyConsumptionDTO(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(HourlyConsumptionDTO::getHour))
                .collect(Collectors.toList());
    }
}