package com.example.demo.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

@com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
public class SyncEventDTO {

    @JsonProperty("event_type")
    private String eventType;

    @JsonProperty("user_id")
    private UUID userId;

    @JsonProperty("device_id")
    private UUID deviceId;
    
    @JsonProperty("max_consumption")
    private Double maxConsumption;    

    public SyncEventDTO() {
    }

    public Double getMaxConsumption() {
        return maxConsumption;
    }

    public void setMaxConsumption(Double maxConsumption) {
        this.maxConsumption = maxConsumption;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(UUID deviceId) {
        this.deviceId = deviceId;
    }

    @Override
    public String toString() {
        return "SyncEventDTO{" +
                "eventType='" + eventType + '\'' +
                ", userId=" + userId +
                ", deviceId=" + deviceId +
                ", maxConsumption=" + maxConsumption +
                '}';
    }
}