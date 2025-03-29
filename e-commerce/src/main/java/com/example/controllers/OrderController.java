package com.example.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import com.example.dto.OrderDTO;
import com.example.entities.Order;
import com.example.services.OrderService;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<Void> place(@AuthenticationPrincipal UserDetails userDetails, UriComponentsBuilder ucb) {
        try {
            OrderDTO order = orderService.placeOrder(userDetails.getUsername());
            URI uri = ucb.path("api/orders/{id}").buildAndExpand(order.getId()).toUri();
            return ResponseEntity.created(uri).build();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getOrderHistory(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            return ResponseEntity.ok(orderService.getOrderHistory(userDetails.getUsername()));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDTO> getOrder(@PathVariable Long orderId) {
        if (orderId == null || orderId < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "orderId must be greater than 0.");
        }
        try {
            return ResponseEntity.ok(orderService.getOrder(orderId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
