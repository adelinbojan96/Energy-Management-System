package com.example.demo.controllers;

import com.example.demo.dtos.HourlyConsumptionDTO;
import com.example.demo.services.MonitoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/monitoring")
@CrossOrigin(origins = "*") 
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/consumption")
    public ResponseEntity<?> getDailyConsumption(
            @RequestParam("date") String dateRaw,
            @RequestParam("userId") String userIdRaw 
    ) {
        System.out.println(">>> DEBUG START REQUEST");
        System.out.println(">>> RAW Date: " + dateRaw);
        System.out.println(">>> RAW UserId: " + userIdRaw);

        try {
            LocalDate date = LocalDate.parse(dateRaw);
            UUID userId = UUID.fromString(userIdRaw);

            System.out.println(">>> PARSING OK. Calling Service...");

            List<HourlyConsumptionDTO> data = monitoringService.getChartDataForUser(userId, date);

            System.out.println(">>> SERVICE RETURNED. Items: " + (data != null ? data.size() : "null"));

            return ResponseEntity.ok(data);

        } catch (Exception e) {
            System.out.println(">>> ERROR IN CONTROLLER:");
            e.printStackTrace(); 
            return ResponseEntity.status(500).body("Server Error: " + e.getMessage());
        }
    }
}