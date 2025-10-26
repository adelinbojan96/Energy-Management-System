package com.example.demo.services;

import com.example.demo.dtos.CredentialDTO;
import com.example.demo.dtos.CredentialDetailsDTO;
import com.example.demo.entities.Credential;
import com.example.demo.repositories.CredentialRepository;
import com.example.demo.security.JwtUtil;
import com.example.demo.security.Role;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);
    private final CredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(CredentialRepository credentialRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Handles user registration.
     * Only admin or first-time startup logic should call this.
     */
    public void register(CredentialDetailsDTO dto) {
        if (credentialRepository.findByUsername(dto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        Credential credential = new Credential();
        credential.setUsername(dto.getUsername());
        credential.setPassword(passwordEncoder.encode(dto.getPassword()));
        credential.setRole(Role.valueOf(dto.getRole().toUpperCase()));

        credentialRepository.save(credential);
        LOGGER.info("Credential {} registered successfully as {}", credential.getUsername(), credential.getRole());
    }

    /**
     * Handles user login and returns a JWT token.
     */
    public String login(CredentialDTO dto) {
        Credential credential = credentialRepository.findByUsername(dto.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid username or password"));

        if (!passwordEncoder.matches(dto.getPassword(), credential.getPassword())) {
            throw new RuntimeException("Invalid username or password");
        }

        LOGGER.info("{} logged in as {}", credential.getUsername(), credential.getRole());
        return jwtUtil.generateToken(credential);
    }
}
