package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.UUID;

public class DeviceDetailsDTO {

    private UUID id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Max consumption is required")
    private Double maxConsumption;

    private String location;

    private UUID userId;

    public DeviceDetailsDTO() {
    }

    public DeviceDetailsDTO(UUID id, String name, String description, Double maxConsumption, String location, UUID userId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.maxConsumption = maxConsumption;
        this.location = location;
        this.userId = userId;
    }

    public DeviceDetailsDTO(String name, String description, Double maxConsumption, String location, UUID userId) {
        this.name = name;
        this.description = description;
        this.maxConsumption = maxConsumption;
        this.location = location;
        this.userId = userId;
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
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public Double getMaxConsumption() {
        return maxConsumption;
    }
    public void setMaxConsumption(Double maxConsumption) {
        this.maxConsumption = maxConsumption;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public UUID getUserId() {
        return userId;
    }
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DeviceDetailsDTO that = (DeviceDetailsDTO) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(description, that.description) &&
                Objects.equals(maxConsumption, that.maxConsumption) &&
                Objects.equals(location, that.location) &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, maxConsumption, location, userId);
    }
}
