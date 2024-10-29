package com.example.ecommerce.model.entity;

import com.example.ecommerce.model.abstracts.BaseEntity;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Entity
public class Product extends BaseEntity {

    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "stock")
    private Integer stock;
}
