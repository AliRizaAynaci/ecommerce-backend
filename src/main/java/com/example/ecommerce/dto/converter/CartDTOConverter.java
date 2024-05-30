package com.example.ecommerce.dto.converter;

import com.example.ecommerce.dto.response.CartDTO;
import com.example.ecommerce.model.entity.Cart;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class CartDTOConverter {

    private final ModelMapper modelMapper;

    public CartDTOConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Cart convertToEntity(CartDTO cartDTO) {
        Cart cart = modelMapper.map(cartDTO, Cart.class);
        return cart;
    }

    public CartDTO convertToDto(Cart cart) {
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        return cartDTO;
    }
}
