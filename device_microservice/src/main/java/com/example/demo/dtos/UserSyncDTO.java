package com.example.demo.dtos;

import java.util.UUID;

public class UserSyncDTO {
    private UUID userId;
    private String action; // "CREATE", "UPDATE", "DELETE"

    public UserSyncDTO() {}

    public UserSyncDTO(UUID userId, String action) {
        this.userId = userId;
        this.action = action;
    }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
}