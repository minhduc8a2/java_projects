package com.example.controller;

import org.springframework.web.bind.annotation.RestController;

import com.example.model.dto.request.LoginRequest;
import com.example.model.dto.request.RegisterRequest;
import com.example.model.dto.response.AuthResponse;
import com.example.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        String jwtToken = authService.authenticate(request.username(), request.password());
        return ResponseEntity.ok(new AuthResponse(jwtToken));

    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String jwtToken = authService.register(request.username(), request.email(), request.password());
        return ResponseEntity.ok(new AuthResponse(jwtToken));

    }

}
