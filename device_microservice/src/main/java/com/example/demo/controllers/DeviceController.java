package com.example.demo.controllers;

import com.example.demo.dtos.DeviceDTO;
import com.example.demo.dtos.DeviceDetailsDTO;
import com.example.demo.services.DeviceService;
import com.example.demo.dtos.SyncEventDTO;
import com.example.demo.entities.Device; // Import necesar pentru entitatea returnată
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.routing.key.device}")
    private String deviceRoutingKey;

    public DeviceController(DeviceService deviceService, RabbitTemplate rabbitTemplate) {
        this.deviceService = deviceService;
        this.rabbitTemplate = rabbitTemplate;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        return ResponseEntity.ok(deviceService.findDevices());
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody DeviceDetailsDTO device) {
        UUID id = deviceService.insert(device);

        try {
            SyncEventDTO syncMsg = new SyncEventDTO();
            syncMsg.setEventType("DEVICE_CREATED");
            syncMsg.setDeviceId(id);
            syncMsg.setUserId(device.getUserId());
            syncMsg.setMaxConsumption(device.getMaxConsumption());

            rabbitTemplate.convertAndSend(exchange, deviceRoutingKey, syncMsg);
            System.out.println("Sent Sync Create Message for Device: " + id);

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
        Device updatedDevice = deviceService.assignDeviceToUser(req.deviceId(), req.userId());

        try {
            SyncEventDTO syncMsg = new SyncEventDTO();
            syncMsg.setEventType("DEVICE_MAPPED"); 
            syncMsg.setDeviceId(updatedDevice.getId());
            syncMsg.setUserId(updatedDevice.getUserId());
            syncMsg.setMaxConsumption(updatedDevice.getMaxConsumption()); 

            rabbitTemplate.convertAndSend(exchange, deviceRoutingKey, syncMsg);
            System.out.println("Sent Sync Assign Message for Device: " + updatedDevice.getId() + " to User: " + updatedDevice.getUserId());

        } catch (Exception e) {
            System.err.println("Error sending assign sync message: " + e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable UUID id, @RequestBody DeviceDTO updatedDevice) {
        DeviceDTO device = deviceService.update(id, updatedDevice);
        
        try {
             SyncEventDTO syncMsg = new SyncEventDTO();
             syncMsg.setEventType("DEVICE_UPDATED");
             syncMsg.setDeviceId(device.getId());
             syncMsg.setMaxConsumption(device.getMaxConsumption());
             
             rabbitTemplate.convertAndSend(exchange, deviceRoutingKey, syncMsg);
        } catch (Exception e) {
             System.err.println("Error sending update sync: " + e.getMessage());
        }

        return ResponseEntity.ok(device);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDetailsDTO> getDevice(@PathVariable UUID id) {
        return ResponseEntity.ok(deviceService.findDeviceById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable UUID id) {
        deviceService.delete(id);

        try {
            SyncEventDTO syncMsg = new SyncEventDTO();
            syncMsg.setEventType("DEVICE_DELETED");
            syncMsg.setDeviceId(id);

            rabbitTemplate.convertAndSend(exchange, deviceRoutingKey, syncMsg);
            System.out.println("Sent Sync Delete Message for Device: " + id);

        } catch (Exception e) {
            System.err.println("Error sending delete sync message: " + e.getMessage());
        }

        return ResponseEntity.noContent().build();
    }
}