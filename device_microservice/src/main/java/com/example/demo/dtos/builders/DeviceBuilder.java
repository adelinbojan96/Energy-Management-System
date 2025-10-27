package com.example.demo.dtos.builders;

import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.entities.Device;

public class DeviceBuilder {

    private DeviceBuilder() {
    }

    public static DeviceDTO toDeviceDTO(Device device) {
        return new DeviceDTO(
                device.getId(),
                device.getName(),
                device.getDescription(),
                device.getMaxConsumption(),
                device.getLocation()
        );
    }

    public static DeviceDetailsDTO toDeviceDetailsDTO(Device device) {
        return new DeviceDetailsDTO(
                device.getId(),
                device.getName(),
                device.getDescription(),
                device.getMaxConsumption(),
                device.getLocation(),
                device.getUserId()
        );
    }

    public static Device toEntity(DeviceDetailsDTO dto) {
        return new Device(
                dto.getName(),
                dto.getDescription(),
                dto.getMaxConsumption(),
                dto.getLocation(),
                dto.getUserId()
        );
    }
}
