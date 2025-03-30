package com.example.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.model.dto.CartItemDTO;
import com.example.model.dto.request.AddToCartRequest;
import com.example.model.entity.Cart;
import com.example.model.entity.CartItem;
import com.example.service.CartService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;

    @GetMapping
    public ResponseEntity<List<CartItemDTO>> getCartItems(@AuthenticationPrincipal UserDetails userDetails) {
        var cartItems = cartService.getCart(userDetails.getUsername()).getCartItems();
        return ResponseEntity.ok(cartItems);
    }

    @PostMapping
    public ResponseEntity<Void> addToCart(@Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.addToCart(userDetails.getUsername(), request.productId(), request.quantity());
        return ResponseEntity.noContent().build();

    }

    @DeleteMapping("cartItems/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable @NotNull @Min(1) Long cartItemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        cartService.removeFromCart(userDetails.getUsername(), cartItemId);
        return ResponseEntity.noContent().build();
    }

}
