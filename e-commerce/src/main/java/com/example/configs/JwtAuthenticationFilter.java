package com.example.configs;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String jwtToken = extractJwtToken(request);
        if (jwtToken != null && jwtUtils.isValid(jwtToken)) {

            UserDetails userDetails = customUserDetailsService.loadUserByUsername(jwtUtils.getUsername(jwtToken));
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(userDetails, jwtToken, userDetails.getAuthorities()));
        }
        filterChain.doFilter(request, response);
    }

    private String extractJwtToken(HttpServletRequest request) {
        String authorizationPart = request.getHeader("Authorization");
        if (authorizationPart != null && authorizationPart.startsWith("Bearer ")) {
            return authorizationPart.substring("Bearer ".length());
        }
        return null;
    }

}
