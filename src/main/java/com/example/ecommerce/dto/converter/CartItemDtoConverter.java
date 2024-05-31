package com.example.ecommerce.dto.converter;

import com.example.ecommerce.dto.response.CartItemDTO;
import com.example.ecommerce.model.entity.CartItem;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CartItemDtoConverter {

    private final ModelMapper modelMapper;

    public CartItemDtoConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CartItemDTO convertToDto(CartItem cartItem) {
        CartItemDTO cartItemDTO = modelMapper.map(cartItem, CartItemDTO.class);
        return cartItemDTO;
    }

    public CartItem convertToEntity(CartItemDTO cartItemDTO) {
        CartItem cartItem = modelMapper.map(cartItemDTO, CartItem.class);
        return cartItem;
    }
}
