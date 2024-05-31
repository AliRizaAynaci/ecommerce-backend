package com.example.ecommerce.dto.response;

import com.example.ecommerce.dto.request.ProductRequestDTO;
import com.example.ecommerce.model.entity.Product;
import lombok.Data;

@Data
public class CartItemDTO {
    private Product product;
    private int quantity;
}
