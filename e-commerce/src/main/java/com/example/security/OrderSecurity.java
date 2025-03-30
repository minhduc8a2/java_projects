package com.example.security;

import org.springframework.stereotype.Component;

import com.example.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class OrderSecurity {

    private final OrderRepository orderRepository;

    private final AuthenticationFacade authenticationFacade; // Helper to get current user

    public boolean isOrderOwner(Long orderId) {
        String currentUsername = authenticationFacade.getAuthenticatedUsername();
        
        return orderRepository.findById(orderId)
                .map(order -> order.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }
}

