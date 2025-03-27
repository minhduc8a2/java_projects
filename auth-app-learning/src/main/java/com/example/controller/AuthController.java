package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import com.example.config.CustomUserDetailsService;
import com.example.config.JwtUtils;
import com.example.entity.RefreshToken;
import com.example.entity.User;
import com.example.entity.User.Role;
import com.example.repository.UserRepository;
import com.example.response.AuthResponse;
import com.example.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

import java.util.Map;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final CustomUserDetailsService customUserDetailsService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("login")
    public ResponseEntity<AuthResponse> login(@RequestParam String username, @RequestParam String password) {
        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(username, password));
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return ResponseEntity.ok(generateAuthResponse(userDetails));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid credentials");
        }
    }

    @PostMapping("register")
    public ResponseEntity<AuthResponse> register(@RequestParam String username, @RequestParam String email,
            @RequestParam String password) {
        try {
            userRepository.save(new User(null, username, email, passwordEncoder.encode(password), Role.USER));
            var userDetails = customUserDetailsService.loadUserByUsername(username);
            return ResponseEntity.ok(generateAuthResponse(userDetails));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid email or username");
        }
    }

    private AuthResponse generateAuthResponse(UserDetails userDetails) {
        String accessToken = jwtUtils.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(userDetails.getUsername());
        return new AuthResponse(accessToken, refreshToken.getToken());
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refreshToken");

        if (refreshTokenService.validateRefreshToken(refreshToken)) {
            String username = refreshTokenService.getUsername(refreshToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            String newAccessToken = jwtUtils.generateToken(userDetails);

            return ResponseEntity.ok(Map.of(
                    "accessToken", newAccessToken));
        } else {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid or expired refresh token");
        }
    }

    

}
