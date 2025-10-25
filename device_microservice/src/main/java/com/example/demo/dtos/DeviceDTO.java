package com.example.demo.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DeviceDTO {

    private Integer id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Max consumption is required")
    private Double maxConsumption;

    public DeviceDTO() {
    }

    public DeviceDTO(Integer id, String name, Double maxConsumption) {
        this.id = id;
        this.name = name;
        this.maxConsumption = maxConsumption;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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
}
