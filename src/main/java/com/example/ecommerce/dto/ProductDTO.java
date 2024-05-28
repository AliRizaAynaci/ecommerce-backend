package com.example.ecommerce.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDTO {

    private String name;
    private BigDecimal price;
}