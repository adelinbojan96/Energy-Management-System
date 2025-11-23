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
            LOGGER.error("Received invalid message: {}", message);
            return;
        }

        LOGGER.info("Received measurement for device {}: {}", message.getDeviceId(), message.getValue());

        LocalDateTime truncatedTimestamp = actualTimestamp.truncatedTo(ChronoUnit.HOURS);
        UUID deviceId = message.getDeviceId();

        Optional<HourlyConsumption> existingRecord = consumptionRepo.findByDeviceIdAndTimestamp(deviceId, truncatedTimestamp);

        if (existingRecord.isPresent()) {
            HourlyConsumption record = existingRecord.get();
            record.setTotalConsumption(record.getTotalConsumption() + message.getValue());
            consumptionRepo.save(record);
            LOGGER.info("Updated consumption for device {}. New total: {}", deviceId, record.getTotalConsumption());
        } else {
            HourlyConsumption newRecord = new HourlyConsumption(deviceId, truncatedTimestamp, message.getValue());
            consumptionRepo.save(newRecord);
            LOGGER.info("Created new consumption record for device {}.", deviceId);
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.sync.monitoring}")
    @Transactional
    public void consumeSyncEvent(SyncEventDTO event) {
        LOGGER.info("Received sync event: {}", event.getEventType());

        if ("DEVICE_MAPPED".equals(event.getEventType()) || "DEVICE_CREATED".equals(event.getEventType())) {
            if (event.getUserId() == null || event.getDeviceId() == null) {
                LOGGER.warn("Received invalid DEVICE/USER sync event with null IDs");
                return;
            }

            DeviceUserMapping mapping = new DeviceUserMapping(event.getDeviceId(), event.getUserId());
            try {
                mappingRepo.save(mapping);
                LOGGER.info("Saved mapping for device {} to user {}", event.getDeviceId(), event.getUserId());
            } catch (Exception e) {
                LOGGER.warn("Could not save mapping (may be duplicate or data integrity issue): {}", e.getMessage());
            }
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