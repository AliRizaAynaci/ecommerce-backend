package com.example.ecommerce.dto.request;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
public class ProductRequestDTO {

    private String name;
    private BigDecimal price;
}
