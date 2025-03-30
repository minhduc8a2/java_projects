package com.example.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.example.model.enums.OrderStatus;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private List<OrderItemDTO> orderItems;
    private LocalDateTime orderDate;

    @NotNull(message = "Order status is required.")
    private OrderStatus orderStatus;
}
