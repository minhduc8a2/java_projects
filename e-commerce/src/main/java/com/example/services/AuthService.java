package com.example.services;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.example.configs.CustomUserDetailsService;
import com.example.configs.JwtUtils;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    public String authenticate(String username, String password) throws AuthenticationException{
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        String jwtToken = jwtUtils.generateJwtToken(userDetails);
        return jwtToken;
    }
}
