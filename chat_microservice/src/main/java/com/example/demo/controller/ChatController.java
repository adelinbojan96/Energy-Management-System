package com.example.demo.controller;

import com.example.demo.model.ChatMessage; 
import com.example.demo.service.CustomerSupportService; 
import org.springframework.messaging.handler.annotation.MessageMapping; 
import org.springframework.messaging.handler.annotation.Payload; 
import org.springframework.messaging.simp.SimpMessagingTemplate; 
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.*;

import java.util.Collections; 
import java.util.Set; 
import java.util.concurrent.ConcurrentHashMap;

@Controller public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CustomerSupportService customerSupportService;
    private final Set<String> activeSupportSessions = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public ChatController(SimpMessagingTemplate messagingTemplate, CustomerSupportService customerSupportService) {
        this.messagingTemplate = messagingTemplate;
        this.customerSupportService = customerSupportService;
    }

    @MessageMapping("/chat")
    public void processMessage(@Payload ChatMessage message) {
        String sender = message.getSenderId();
        String content = message.getContent() != null ? message.getContent().toLowerCase() : "";

        if ("admin".equalsIgnoreCase(sender)) {
            String userTopic = "/topic/" + message.getRecipientId();
            
            activeSupportSessions.add(message.getRecipientId());

            messagingTemplate.convertAndSend(userTopic, message);
            return;
        }

        messagingTemplate.convertAndSend("/topic/admin", message);

        if (activeSupportSessions.contains(sender)) {
            return; 
        }

        if (content.contains("human") || content.contains("admin") || content.contains("support")) {
            activeSupportSessions.add(sender);
            
            ChatMessage handoverMsg = new ChatMessage("System", "System", sender, "I am connecting you to a human administrator now. Please wait...", "CHAT");
            messagingTemplate.convertAndSend("/topic/" + sender, handoverMsg);
            return;
        }

        ChatMessage botResponse = customerSupportService.processMessage(message);
        messagingTemplate.convertAndSend("/topic/" + sender, botResponse);
    }

    @PostMapping("/chat/test/send")
    @ResponseBody
    public String testReceiveMessage(@RequestBody ChatMessage message) {
        processMessage(message); 
        return "Message Injected!";
    }

    @GetMapping("/chat/trigger-alert")
    @ResponseBody
    public String triggerFakeAlert() {
        String fakeAlertMessage = "Device exceeded limit! Value: 9999.0";
        messagingTemplate.convertAndSend("/topic/alerts", fakeAlertMessage);
        return "Alert sent!";
    }
}