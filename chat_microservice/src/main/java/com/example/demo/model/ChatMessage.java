package com.example.demo.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String senderId;   
    private String senderName;  
    private String recipientId; 
    private String content;     
    private String type;        
}