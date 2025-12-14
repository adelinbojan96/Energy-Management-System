package com.example.demo.service;

import com.example.demo.config.RabbitConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class NotificationListener {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationListener(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @RabbitListener(queues = RabbitConfig.QUEUE_NAME)
    public void handleOverconsumption(String message) {
        System.out.println("ALERT RECEIVED: " + message);
        
        messagingTemplate.convertAndSend("/topic/alerts", message);
    }
}