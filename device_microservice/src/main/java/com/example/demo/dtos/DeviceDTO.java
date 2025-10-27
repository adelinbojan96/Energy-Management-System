package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public class DeviceDTO {

    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Max consumption is required")
    private Double maxConsumption;

    private String description;

    private String location;

    public DeviceDTO() {
    }

    public DeviceDTO(UUID id, String name, String description, Double maxConsumption, String location) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxConsumption = maxConsumption;
        this.location = location;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(Double maxConsumption) {
        this.maxConsumption = maxConsumption;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
