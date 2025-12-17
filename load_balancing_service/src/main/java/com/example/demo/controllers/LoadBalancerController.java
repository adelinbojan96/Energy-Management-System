package com.example.demo.controllers;

import com.example.demo.service.ConsistentHashing;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/api/monitoring")
public class LoadBalancerController {

    private final ConsistentHashing consistentHashing;
    private final RestTemplate restTemplate;

    private final Map<String, String> replicaUrlMap = Map.of(
            "monitoring_queue_1", "http://monitoring-replica-1:8084", 
            "monitoring_queue_2", "http://monitoring-replica-2:8085"
    );

    public LoadBalancerController(ConsistentHashing consistentHashing) {
        this.consistentHashing = consistentHashing;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping("/consumption")
    public ResponseEntity<?> forwardConsumptionRequest(
            @RequestParam("date") String date,
            @RequestParam("userId") String userId
    ) {
        String targetQueue = consistentHashing.getTargetReplica(userId);
        
        String targetUrlBase = "";
        
        if ("monitoring_queue_1".equals(targetQueue)) {
             targetUrlBase = "http://monitoring-replica-1:8084"; 
        } else {
             targetUrlBase = "http://monitoring-replica-2:8085"; 
        }

        String fullUrl = targetUrlBase + "/api/monitoring/consumption?date=" + date + "&userId=" + userId;

        System.out.println("LB HTTP LOG: Redirecting User " + userId + " -> " + fullUrl);

        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(fullUrl, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Load Balancer Forwarding Error: " + e.getMessage());
        }
    }
}