package com.example.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.configs.CustomUserDetailsService;
import com.example.configs.JwtUtils;
import com.example.entities.User;
import com.example.entities.User.Role;
import com.example.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public String authenticate(String username, String password) throws AuthenticationException {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        String jwtToken = jwtUtils.generateJwtToken(userDetails);
        return jwtToken;
    }

    public String register(String username, String email, String password) throws Exception {
        User user = userRepository.save(new User(null, username, email, passwordEncoder.encode(password), Role.USER));
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getUsername());
        String jwtToken = jwtUtils.generateJwtToken(userDetails);
        return jwtToken;
    }
}
