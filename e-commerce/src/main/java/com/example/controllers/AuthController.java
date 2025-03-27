package com.example.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.requests.LoginRequest;
import com.example.responses.AuthReponse;
import com.example.services.AuthService;

import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthReponse> login(@RequestBody LoginRequest request) {

        String username = request.username();
        String password = request.password();
        if (username == null || password == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username and password are required.");
        }
        try {
            String jwtToken = authService.authenticate(username, password);
            return ResponseEntity.ok(new AuthReponse(jwtToken));
        } catch (AuthenticationException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Invalid username or password.");
        }
    }

}
