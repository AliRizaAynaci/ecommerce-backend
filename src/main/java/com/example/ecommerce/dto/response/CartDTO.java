package com.example.ecommerce.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private Long customerId;
    private List<CartItemDTO> items;
}
