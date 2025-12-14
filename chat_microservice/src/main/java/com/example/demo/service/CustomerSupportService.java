package com.example.demo.service;

import com.example.demo.model.ChatMessage;
import org.springframework.stereotype.Service;

@Service
public class CustomerSupportService {

    public ChatMessage processMessage(ChatMessage message) {
        String input = message.getContent().toLowerCase().trim();
        String responseText;

        // RULES
        
        // Greetings
        if (input.contains("hello") || input.contains("hi")) {
            responseText = "Hello! I am your EMS Assistant. How can I help you regarding your energy consumption?";
        }
        // Price inquiries
        else if (input.contains("price") || input.contains("cost")) {
            responseText = "The standard energy rate is currently $0.15 per kWh. Rates may vary during peak hours.";
        }
        // Adding devices
        else if (input.contains("add device") || input.contains("new device")) {
            responseText = "To add a new smart meter, please navigate to the 'Device Management' page and click 'New Device'.";
        }
        // Alerts/Overconsumption
        else if (input.contains("alert") || input.contains("warning")) {
            responseText = "If you received an overconsumption alert, please check high-power appliances (AC, Heater) immediately.";
        }
        // Viewing history/charts
        else if (input.contains("chart") || input.contains("history") || input.contains("graph")) {
            responseText = "You can view your historical consumption on the Dashboard. Select a date to see the hourly breakdown.";
        }
        // Billing
        else if (input.contains("bill") || input.contains("invoice")) {
            responseText = "Invoices are generated automatically on the 1st of every month.";
        }
        // Simulator info (Project specific)
        else if (input.contains("simulator") || input.contains("data")) {
            responseText = "The simulator generates sensor readings every 10 minutes. Please ensure the backend is running.";
        }
        // Account issues
        else if (input.contains("password") || input.contains("login")) {
            responseText = "You can reset your password by clicking 'Forgot Password' on the login screen.";
        }
        // Hours of operation
        else if (input.contains("hours") || input.contains("time")) {
            responseText = "Our support team is available from 9:00 AM to 5:00 PM (EET). Automated support is 24/7.";
        }
        // Contacting Human Admin
        else if (input.contains("human") || input.contains("admin") || input.contains("support")) {
            responseText = "I am forwarding this conversation to a human administrator. They will join shortly.";
        }

        // Default response
        else {
            responseText = "I'm not sure about that. I will forward your message to the administrator.";
        }
        return new ChatMessage("System", responseText, "CHAT");
    }
}