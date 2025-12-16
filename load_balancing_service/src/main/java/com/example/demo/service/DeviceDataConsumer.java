package com.example.demo.service;

import com.example.demo.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class DeviceDataConsumer {

    private final RabbitTemplate rabbitTemplate;
    private final ConsistentHashing consistentHashing;

    public DeviceDataConsumer(RabbitTemplate rabbitTemplate, ConsistentHashing consistentHashing) {
        this.rabbitTemplate = rabbitTemplate;
        this.consistentHashing = consistentHashing;
    }

    @RabbitListener(queues = RabbitConfig.SIMULATOR_QUEUE)
    public void processAndRoute(Map<String, Object> messageMap) {
        try {
            String deviceId = "unknown";
            if (messageMap.containsKey("device_id")) {
                deviceId = messageMap.get("device_id").toString();
            } else if (messageMap.containsKey("deviceId")) {
                deviceId = messageMap.get("deviceId").toString();
            } else if (messageMap.containsKey("id")) {
                deviceId = messageMap.get("id").toString();
            }

            String targetQueue = consistentHashing.getTargetReplica(deviceId);

            System.out.println("LB LOG: Routing Device " + deviceId + " -> " + targetQueue);

            rabbitTemplate.convertAndSend(targetQueue, messageMap);

        } catch (Exception e) {
            System.err.println("LB ERROR: Failed to route message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}