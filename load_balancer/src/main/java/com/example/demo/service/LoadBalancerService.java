package com.example.demo.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class LoadBalancerService {

    private final RabbitTemplate rabbitTemplate;

    @Value("${loadbalancer.queue.output.base}")
    private String queueBaseName;

    @Value("${loadbalancer.queue.output.count}")
    private int replicaCount;

    @Value("${loadbalancer.exchange.output}")
    private String exchangeName;

    public LoadBalancerService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @RabbitListener(queues = "${loadbalancer.queue.input}")
    public void processMeasurement(Map<String, Object> measurement) {
        try {
            String deviceIdStr = String.valueOf(measurement.get("device_id"));
            long deviceId = Long.parseLong(deviceIdStr);
            long index = Math.abs(deviceId % replicaCount);
            String routingKey = queueBaseName + index;
            
            System.out.println("Load Balancer: Routing Device " + deviceId + " to " + routingKey);
            rabbitTemplate.convertAndSend(exchangeName, routingKey, measurement);

        } catch (Exception e) {
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}