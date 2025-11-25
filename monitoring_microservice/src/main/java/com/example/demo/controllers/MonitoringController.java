package com.example.demo.controllers;

import com.example.demo.dtos.HourlyConsumptionDTO;
import com.example.demo.services.MonitoringService;
import org.springframework.format.annotation.DateTimeFormat;
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
    public ResponseEntity<List<HourlyConsumptionDTO>> getDailyConsumption(
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("userId") UUID userId 
    ) {

        List<HourlyConsumptionDTO> data = monitoringService.getChartDataForUser(userId, date);
        return ResponseEntity.ok(data);
    }
}