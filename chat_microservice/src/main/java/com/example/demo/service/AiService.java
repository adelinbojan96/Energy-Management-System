package com.example.demo.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiService {

    private static final String MODEL_NAME = "tinyllama"; 
    private static final String OLLAMA_URL = "http://ollama:11434/v1/chat/completions";
    
    private final RestTemplate restTemplate = new RestTemplate();

    public String callGpt(String userMessage) {
        try {
        
            Thread.sleep(1500); 

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
        

            Map<String, Object> body = new HashMap<>();
            body.put("model", MODEL_NAME); 
        
            body.put("stream", false); 
            
            Map<String, String> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", userMessage);
            
            body.put("messages", List.of(message));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            
            ResponseEntity<Map> response = restTemplate.postForEntity(OLLAMA_URL, entity, Map.class);

            Map<String, Object> responseBody = response.getBody();
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            return (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");

        } catch (Exception e) {
            System.err.println("AI Connection Error: " + e.getMessage());
            e.printStackTrace();
            return "I am having trouble connecting to my local AI brain (Ollama) right now.";
        }
    }
}