package com.example.demo.model;

import lombok.*; 

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessage {
    private String senderId; // user ID or "Admin"
    private String content;  // text
    private String type;     // "CHAT" or "TYPING"
}