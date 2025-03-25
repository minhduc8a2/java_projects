package com.example.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.entity.User;
import com.example.entity.User.Role;
import com.example.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public void register(String username, String email, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        userRepository.save(new User(null, username, email, encodedPassword, Role.USER));
    }
}
