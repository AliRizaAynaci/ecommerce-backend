package com.example.ecommerce.dto.response;

import com.example.ecommerce.model.entity.Product;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Product product;
    private int quantity;
    private BigDecimal priceAtPurchase;
}
