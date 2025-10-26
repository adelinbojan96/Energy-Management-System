package com.example.demo.services;

import com.example.demo.entities.Credential;
import com.example.demo.repositories.CredentialRepository;
import com.example.demo.security.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class DataInitializer implements CommandLineRunner {

    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(CredentialRepository credentialRepository, PasswordEncoder passwordEncoder) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (credentialRepository.count() == 0) {

            Credential admin = new Credential("admin", passwordEncoder.encode("admin"), Role.ADMIN);
            Credential saved = credentialRepository.save(admin);
            System.out.println("Default admin credential created: username='admin', password='admin'");

            try {
                RestTemplate restTemplate = new RestTemplate();
                String userServiceUrl = "http://localhost:8081/users";

                Map<String, Object> userPayload = new HashMap<>();
                userPayload.put("name", "Admin");
                userPayload.put("age", 30);
                userPayload.put("email", "admin@system.com");
                userPayload.put("credentialId", saved.getId());

                restTemplate.postForObject(userServiceUrl, userPayload, Void.class);
                System.out.println("Default admin profile created in user service.");
            } catch (Exception e) {
                System.err.println("⚠️ Failed to create admin user in user service: " + e.getMessage());
            }
        }
    }
}
