package com.example.demo.controller;

import com.example.demo.model.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat") 
    public void processMessage(@Payload ChatMessage message) {

        if ("hello".equalsIgnoreCase(message.getContent())) {
            ChatMessage reply = new ChatMessage("System", "Hello! How can I help?", "CHAT");
            
            messagingTemplate.convertAndSendToUser(
                message.getSenderId(), 
                "/queue/messages", 
                reply
            );
        } else {
            System.out.println("Chat did not work as expected");
        }
    }
}