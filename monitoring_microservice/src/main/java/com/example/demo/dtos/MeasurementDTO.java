package com.example.demo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class MeasurementDTO {

    private Long timestamp;

    @JsonProperty("device_id")
    private UUID deviceId;

    @JsonProperty("measurement_value")
    private Double value;

    public MeasurementDTO() {
    }

    public LocalDateTime getLocalDateTime() {
        if (timestamp == null) return null;
        return Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "MeasurementDTO{" +
                "timestamp=" + timestamp +
                ", deviceId=" + deviceId +
                ", value=" + value +
                '}';
    }
}