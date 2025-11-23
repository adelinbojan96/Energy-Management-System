package com.example.demo.controllers;

import com.example.demo.dtos.CredentialDTO;
import com.example.demo.dtos.CredentialDetailsDTO;
import com.example.demo.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // register
    @PostMapping("/register")
    public ResponseEntity<Map<String, UUID>> register(@Valid @RequestBody CredentialDetailsDTO userDetails) {
        UUID id = authService.register(userDetails);
        return ResponseEntity.ok(Map.of("id", id));
    }

    // login
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody CredentialDTO loginRequest) {
        String token = authService.login(loginRequest);
        return ResponseEntity.ok(token);  // return JWT token to frontend
    }

    //  deletion
    @DeleteMapping("/credentials/{id}")
    public ResponseEntity<Void> deleteCredential(@PathVariable UUID id) throws Exception {
        authService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
