package com.example.controller;

import com.example.config.CustomUserDetailsService;
import com.example.config.JwtUtils;
import com.example.service.AuthService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AuthService authService;
    private final CustomUserDetailsService userDetailsService;

    @GetMapping("")
    public String hello() {
        return "Welcome to AuthController";
    }

    @PostMapping("login")
    public String login(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication auth = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            return jwtUtils.generateToken(userDetails);
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

    }

    @PostMapping("register")
    public String register(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        try {
            // Register user
            authService.register(username, email, password);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            // Generate JWT token
            String token = jwtUtils.generateToken(userDetails);

            // Return response with token
            return token;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid information");
        }
    }

}
