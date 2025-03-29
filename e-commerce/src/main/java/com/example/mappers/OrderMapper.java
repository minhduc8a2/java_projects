package com.example.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.example.dto.CartDTO;
import com.example.dto.CartItemDTO;
import com.example.dto.OrderDTO;
import com.example.dto.OrderItemDTO;
import com.example.entities.CartItem;
import com.example.entities.Order;
import com.example.entities.OrderItem;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {

    OrderDTO orderToOrderDTO(Order order);

    Order orderDTOToOrder(OrderDTO orderDTO);

    

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.description", target = "productDescription")
    OrderItemDTO orderItemToOrderItemDTO(OrderItem orderItem);

    List<OrderItemDTO> orderItemsToOrderItemDTOs(List<OrderItem> orderItems);
}
