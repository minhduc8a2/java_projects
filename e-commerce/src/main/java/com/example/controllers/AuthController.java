package com.example.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.requests.LoginRequest;
import com.example.requests.RegisterRequest;
import com.example.responses.AuthReponse;
import com.example.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthReponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            String jwtToken = authService.authenticate(request.username(), request.password());
            return ResponseEntity.ok(new AuthReponse(jwtToken));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid username or password.");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<AuthReponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            String jwtToken = authService.register(request.username(), request.email(), request.password());
            return ResponseEntity.ok(new AuthReponse(jwtToken));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid username or email");
        }
    }

}
