package com.example.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import com.example.dto.CartDTO;
import com.example.dto.CartItemDTO;
import com.example.entities.Cart;
import com.example.entities.CartItem;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartMapper {

    CartDTO cartToCartDTO(Cart cart);

    Cart cartDTOToCart(CartDTO cartDTO);


    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.description", target = "productDescription")
    @Mapping(source = "product.price", target = "price")
    CartItemDTO cartItemToCartItemDTO(CartItem cartItem);

    List<CartItemDTO> cartItemsToCartItemDTOs(List<CartItem> cartItems);
}
