package com.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.dto.CartDTO;
import com.example.dto.CartItemDTO;
import com.example.entities.Cart;
import com.example.entities.CartItem;
import com.example.entities.Product;
import com.example.entities.User;
import com.example.mappers.CartMapper;
import com.example.repositories.CartItemRepository;
import com.example.repositories.CartRepository;
import com.example.repositories.ProductRepository;
import com.example.repositories.UserRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CartService {
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartItemRepository cartItemRepository;

    private final CartMapper cartMapper;

    @Transactional
    public void addToCart(String username, Long productId, int quantity) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found"));

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        CartItem cartItem = new CartItem(null, cart, product, quantity);
        cartItemRepository.save(cartItem);
    }

    @Transactional
    public void removeFromCart(String username, Long itemId) {
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        if (!item.getCart().getUser().getUsername().equals(username)) {
            throw new RuntimeException("Not owner");
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public CartDTO getCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        return cartMapper.cartToCartDTO(cart);
    }

    @Transactional
    public List<CartItemDTO> getCartItems(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
        return cartMapper.cartItemsToCartItemDTOs(cart.getCartItems());
    }

    @Transactional
    public void clearCart(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });
        cartItemRepository.deleteAll(cart.getCartItems());
    }

}
