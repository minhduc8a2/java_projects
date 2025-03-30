package com.example.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.example.model.dto.CartDTO;
import com.example.model.dto.CartItemDTO;
import com.example.model.dto.OrderDTO;
import com.example.model.dto.OrderItemDTO;
import com.example.model.entity.CartItem;
import com.example.model.entity.Order;
import com.example.model.entity.OrderItem;

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
