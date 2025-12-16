package com.example.demo.service;

import com.example.demo.model.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public class CustomerSupportService {

 
    private final AiService aiService;

    public CustomerSupportService(AiService aiService) {
        this.aiService = aiService;
    }

    public ChatMessage processMessage(ChatMessage message) {
        String input = message.getContent() != null ? message.getContent().toLowerCase().trim() : "";
        String keyword = detectKeyword(input);
        String responseText;

        switch (keyword) {
            case "greeting" -> responseText = "Hello! I am your EMS Assistant. How can I help you regarding your energy consumption?";
            case "price" -> responseText = "The standard energy rate is currently $0.15 per kWh. Rates may vary during peak hours.";
            case "add_device" -> responseText = "To add a new smart meter, please navigate to the 'Device Management' page and click 'New Device'.";
            case "alert" -> responseText = "If you received an overconsumption alert, please check high-power appliances (AC, Heater) immediately.";
            case "chart" -> responseText = "You can view your historical consumption on the Dashboard. Select a date to see the hourly breakdown.";
            case "bill" -> responseText = "Invoices are generated automatically on the 1st of every month.";
            case "simulator" -> responseText = "The simulator generates sensor readings every 10 minutes. Please ensure the backend is running.";
            case "password" -> responseText = "You can reset your password by clicking 'Forgot Password' on the login screen.";
            case "hours" -> responseText = "Our support team is available from 9:00 AM to 5:00 PM (EET). Automated support is 24/7.";
            case "human" -> responseText = "I am forwarding this conversation to a human administrator. They will join shortly.";
            
            case "ai" -> {
                String cleanPrompt = message.getContent().replaceAll("(?i)AI", "").trim();
                if(cleanPrompt.isEmpty()) {
                    responseText = "Yes? I am listening. What do you need to know?";
                } else {
             
                    responseText = aiService.callGpt(cleanPrompt);
                }
            }
            
            default -> responseText = "I'm not sure about that. Try starting your sentence with 'AI' to ask the assistant, or I can forward you to an admin.";
        }

        return new ChatMessage("System", "System", message.getSenderId(), responseText, "CHAT");
    }

    private String detectKeyword(String input) {
        if (input.contains("hello") || input.contains("hi")) return "greeting";
        if (input.contains("price") || input.contains("cost")) return "price";
        if (input.contains("add device") || input.contains("new device")) return "add_device";
        if (input.contains("alert") || input.contains("warning")) return "alert";
        if (input.contains("chart") || input.contains("history") || input.contains("graph")) return "chart";
        if (input.contains("bill") || input.contains("invoice")) return "bill";
        if (input.contains("simulator") || input.contains("data")) return "simulator";
        if (input.contains("password") || input.contains("login")) return "password";
        if (input.contains("hours") || input.contains("time")) return "hours";
        if (input.contains("human") || input.contains("admin") || input.contains("support")) return "human";
        
        if (input.startsWith("ai ") || input.equals("ai")) return "ai";
        
        return "unknown";
    }
}