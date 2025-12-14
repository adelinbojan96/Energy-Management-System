package com.example.demo.services;

import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.dtos.builders.DeviceBuilder;
import com.example.demo.entities.Device;
import com.example.demo.handlers.exceptions.model.ResourceNotFoundException;
import com.example.demo.repositories.DeviceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DeviceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public List<DeviceDTO> findDevices() {
        List<Device> userList = deviceRepository.findAll();
        return userList.stream()
                .map(DeviceBuilder::toDeviceDTO)
                .collect(Collectors.toList());
    }

    public DeviceDetailsDTO findDeviceById(UUID id) {
        Optional<Device> prosumerOptional = deviceRepository.findById(id);
        if (prosumerOptional.isEmpty()) {
            LOGGER.error("Device with id {} was not found in db", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }
        return DeviceBuilder.toDeviceDetailsDTO(prosumerOptional.get());
    }

    public UUID insert(DeviceDetailsDTO personDTO) {
        Device device = DeviceBuilder.toEntity(personDTO);
        device = deviceRepository.save(device);
        LOGGER.debug("Person with id {} was inserted in db", device.getId());
        return device.getId();
    }

    public void delete(UUID id) {
        Optional<Device> deviceOptional = deviceRepository.findById(id);
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with id {} not found", id);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + id);
        }

        deviceRepository.deleteById(id);
        LOGGER.info("Deleted device with id {}", id);
    }

    public DeviceDTO update(UUID id, DeviceDTO updatedDevice) {
        Device existing = deviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Device not found"));

        existing.setName(updatedDevice.getName());
        existing.setDescription(updatedDevice.getDescription());
        existing.setMaxConsumption(updatedDevice.getMaxConsumption());
        existing.setLocation(updatedDevice.getLocation());
        existing.setUserId(updatedDevice.getUserId());

        deviceRepository.save(existing);
        return DeviceBuilder.toDeviceDTO(existing);
    }


    public Device assignDeviceToUser(UUID deviceId, UUID userId) {
        Optional<Device> deviceOptional = deviceRepository.findById(deviceId);
        if (deviceOptional.isEmpty()) {
            LOGGER.error("Device with the id {} not found", deviceId);
            throw new ResourceNotFoundException(Device.class.getSimpleName() + " with id: " + deviceId);
        }

        Device device = deviceOptional.get();
        device.setUserId(userId);
        return deviceRepository.save(device); 
    }
}