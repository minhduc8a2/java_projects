package com.example.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.example.model.dto.CartDTO;
import com.example.model.dto.CartItemDTO;
import com.example.model.entity.Cart;
import com.example.model.entity.CartItem;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    CartDTO cartToCartDTO(Cart cart);

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.description", target = "productDescription")
    @Mapping(source = "product.price", target = "price")
    CartItemDTO cartItemToCartItemDTO(CartItem cartItem);

    List<CartItemDTO> cartItemsToCartItemDTOs(List<CartItem> cartItems);

   
}
