package com.example.ecommerce.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private Long customerId;
    private BigDecimal totalPrice;
    private List<CartItemDTO> cartItems;
}
