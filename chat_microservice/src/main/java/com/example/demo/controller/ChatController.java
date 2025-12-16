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

@Controller
public class ChatController {

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

        String content = message.getContent() != null ? message.getContent().toLowerCase().trim() : "";


        if ("admin".equalsIgnoreCase(sender)) {
            activeSupportSessions.add(message.getRecipientId()); 
            messagingTemplate.convertAndSend("/topic/" + message.getRecipientId(), message);
            return;
        }


        messagingTemplate.convertAndSend("/topic/admin", message);


        if (content.equals("exit") || content.equals("bye")) {
            activeSupportSessions.remove(sender);
            ChatMessage exitMsg = new ChatMessage("System", "System", sender, "You have disconnected from the administrator. I am back online.", "CHAT");
            messagingTemplate.convertAndSend("/topic/" + sender, exitMsg);
            return;
        }


        if (activeSupportSessions.contains(sender)) {
            return; 
        }


        if (content.contains("human") || content.contains("admin") || content.contains("support")) {
            activeSupportSessions.add(sender);
            ChatMessage handoverMsg = new ChatMessage("System", "System", sender, "I am connecting you to a human administrator now. Type 'exit' to cancel.", "CHAT");
            messagingTemplate.convertAndSend("/topic/" + sender, handoverMsg);
            return;
        }


        ChatMessage botResponse = customerSupportService.processMessage(message);
        

        if(content.startsWith("ai")) {
            botResponse.setSenderId("AI Assistant");
        }

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