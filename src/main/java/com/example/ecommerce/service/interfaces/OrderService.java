package com.example.ecommerce.service.interfaces;

import com.example.ecommerce.dto.response.OrderResponseDTO;

import java.util.List;

public interface OrderService {
    OrderResponseDTO placeOrder(Long cartId);
    OrderResponseDTO getorderForCode(String orderCode);
    List<OrderResponseDTO> getAllOrdersForCustomer(Long customerId);

}
