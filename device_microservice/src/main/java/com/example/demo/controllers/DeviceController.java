package com.example.demo.controllers;

import com.example.demo.config.RabbitMQConfig; // Make sure you have the Config class created
import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.services.DeviceService;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate; // 1. Import RabbitTemplate
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/device")
@Validated
public class DeviceController {

    private final DeviceService deviceService;
    private final RabbitTemplate rabbitTemplate;

    public DeviceController(DeviceService deviceService, RabbitTemplate rabbitTemplate) {
        this.deviceService = deviceService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        return ResponseEntity.ok(deviceService.findDevices());
    }

    public record DeviceSyncMessage(UUID deviceId, UUID userId, Double maxConsumption) {}

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody DeviceDetailsDTO device) {
        UUID id = deviceService.insert(device);

        try {
            DeviceSyncMessage message = new DeviceSyncMessage(
                    id,
                    device.getUserId(),
                    device.getMaxConsumption()
            );

            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY,
                    message
            );

            System.out.println("Sent Sync Message for Device: " + id);

        } catch (Exception e) {
            System.err.println("Error sending sync message: " + e.getMessage());
        }

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(id)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    public record AssignRequest(UUID deviceId, UUID userId) {}

    @PostMapping("/assign")
    public ResponseEntity<Void> assignDeviceToUserPost(@Valid @RequestBody AssignRequest req) {
        deviceService.assignDeviceToUser(req.deviceId(), req.userId());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable UUID id, @RequestBody DeviceDTO updatedDevice) {
        DeviceDTO device = deviceService.update(id, updatedDevice);
        return ResponseEntity.ok(device);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findDeviceById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}