package com.example.ecommerce.dto.response;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderResponseDTO {
    private Long id;
    private Long customerId;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalPrice;
    private List<OrderItemDTO> items;
}
