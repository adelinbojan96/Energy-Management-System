package com.example.demo.services;

import com.example.demo.dtos.CredentialDTO;
import com.example.demo.dtos.CredentialDetailsDTO;
import com.example.demo.entities.Credential;
import com.example.demo.repositories.CredentialRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AmqpTemplate rabbitTemplate;

    @Value("${rabbitmq.exchange.name}")
    private String syncExchange;

    public AuthService(CredentialRepository credentialRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AmqpTemplate rabbitTemplate) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.rabbitTemplate = rabbitTemplate;
    }

    public UUID register(CredentialDetailsDTO dto) {
        if (credentialRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Credential credential = new Credential();
        credential.setUsername(dto.getUsername());
        credential.setPassword(passwordEncoder.encode(dto.getPassword()));
        credential.setRole(Role.valueOf(dto.getRole().toUpperCase()));

        Credential saved = credentialRepository.save(credential);
        LOGGER.info("Credential {} registered successfully as {}", credential.getUsername(), credential.getRole());

        Map<String, String> syncMessage = new HashMap<>();
        syncMessage.put("event_type", "USER_CREATED");
        syncMessage.put("user_id", saved.getId().toString());
        syncMessage.put("username", saved.getUsername());

        try {
            rabbitTemplate.convertAndSend(syncExchange, "", syncMessage);
            LOGGER.info("Published USER_CREATED event for user_id: {}", saved.getId());
        } catch (Exception e) {
            LOGGER.error("Failed to publish USER_CREATED event for user_id: {}", saved.getId(), e);
        }

        return saved.getId();
    }

    public String login(CredentialDTO dto) {
        Credential credential = credentialRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), credential.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        LOGGER.info("{} logged in as {}", credential.getUsername(), credential.getRole());
        return jwtUtil.generateToken(credential);
    }

    public void delete(UUID id) throws Exception {
        Optional<Credential> verifyCredential = credentialRepository.findById(id);

        if (verifyCredential.isEmpty()) {
            LOGGER.error("Credential with id {} was not found in db", id);
            throw new Exception("Credential with id: " + id + " not found");
        }

        Credential credential = verifyCredential.get();
        if ("admin".equalsIgnoreCase(credential.getUsername())) {
            LOGGER.error("Attempted to delete admin credential with id {}", id);
            throw new Exception("Cannot delete admin credential");
        }

        credentialRepository.deleteById(id);
        LOGGER.info("Deleted credential with id {} and username {}", id, credential.getUsername());
    }
}