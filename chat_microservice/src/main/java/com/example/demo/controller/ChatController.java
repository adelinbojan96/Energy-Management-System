package com.example.demo.controller;

import com.example.demo.model.ChatMessage;
import com.example.demo.service.CustomerSupportService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping; 
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CustomerSupportService customerSupportService;

    public ChatController(SimpMessagingTemplate messagingTemplate, CustomerSupportService customerSupportService) {
        this.messagingTemplate = messagingTemplate;
        this.customerSupportService = customerSupportService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage message) {
        System.out.println("--- CHAT INCOMING LOG ---");
        System.out.println("RECEIVED: Sender ID: " + message.getSenderId() + " | Content: " + message.getContent());
        
        ChatMessage response = customerSupportService.processMessage(message);
        System.out.println("SYSTEM RESPONSE: Sender: " + response.getSenderId() + " | Content: " + response.getContent());
        
        String destinationTopic = "/topic/" + message.getSenderId();        
        System.out.println("SENDING REPLY TO TOPIC: " + destinationTopic);

        messagingTemplate.convertAndSend(
            destinationTopic, 
            response
        );
        System.out.println("--- CHAT END LOG ---");
    }
    
    @GetMapping("/chat/trigger-alert")
    @ResponseBody
    public String triggerFakeAlert() {
        String fakeAlertMessage = "Device ID: d56fc032... exceeded limit! Value: 9999.0";
        
        System.out.println("MANUALLY TRIGGERING ALERT: " + fakeAlertMessage);
        
        messagingTemplate.convertAndSend("/topic/alerts", fakeAlertMessage);
        
        return "Alert sent! Check your dashboard.";
    }
}