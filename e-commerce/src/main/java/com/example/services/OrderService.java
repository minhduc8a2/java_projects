package com.example.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.entities.Cart;
import com.example.entities.Order;
import com.example.entities.OrderItem;
import com.example.entities.Product;
import com.example.entities.User;
import com.example.repositories.*;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final CartItemRepository cartItemRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    public Order placeOrder(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = cart.getItems().stream().map(item -> {
            Product product = item.getProduct();
            return new OrderItem(null, order, product, item.getQuantity(), product.getPrice());
        }).toList();

        order.setOrderItems(orderItems);

        cartItemRepository.deleteAll(cart.getItems());

        return orderRepository.save(order);

    }

    public List<Order> getOrderHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return orderRepository.findByUser(user);
    }

}
