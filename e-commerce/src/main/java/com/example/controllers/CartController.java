package com.example.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.dto.CartItemDTO;
import com.example.entities.Cart;
import com.example.entities.CartItem;
import com.example.requests.AddToCartRequest;
import com.example.services.CartService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
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
        try {
            var cartItems = cartService.getCart(userDetails.getUsername()).getCartItems();
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<Void> addToCart(@Valid @RequestBody AddToCartRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            cartService.addToCart(userDetails.getUsername(), request.productId(), request.quantity());
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("cartItems/{cartItemId}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Long cartItemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (cartItemId == null || cartItemId < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cartItemId must be greater than 0.");
        }
        try {
            cartService.removeFromCart(userDetails.getUsername(), cartItemId);
            return ResponseEntity.noContent().build();

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
