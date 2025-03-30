package com.example.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mappers.OrderMapper;
import com.example.model.dto.OrderDTO;
import com.example.model.entity.Cart;
import com.example.model.entity.Order;
import com.example.model.entity.OrderItem;
import com.example.model.entity.Product;
import com.example.model.entity.User;
import com.example.repository.*;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

   
    private final CartItemRepository cartItemRepository;
   
    private final OrderRepository orderRepository;
   
    private final CartRepository cartRepository;
   
    private final UserRepository userRepository;

    
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDTO placeOrder(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        Cart cart = cartRepository.findByUser(user).orElseThrow(() -> new RuntimeException("Cart not found"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());

        List<OrderItem> orderItems = cart.getCartItems().stream().map(item -> {
            Product product = item.getProduct();
            return new OrderItem(null, order, product, item.getQuantity(), product.getPrice());
        }).toList();

        order.setOrderItems(orderItems);
        cartItemRepository.deleteByCartId(cart.getId());

        return orderMapper.orderToOrderDTO(orderRepository.save(order));

    }

    @Transactional
    public List<OrderDTO> getOrderHistory(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return orderRepository.findByUser(user).stream().map(orderMapper::orderToOrderDTO).toList();
    }

    @Transactional
    @PreAuthorize("#order.user.username == authentication.name")
    public OrderDTO getOrder(long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Id not found: " + id));
        return orderMapper.orderToOrderDTO(order);
    }

}
