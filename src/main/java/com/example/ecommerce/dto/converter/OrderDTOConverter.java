package com.example.ecommerce.dto.converter;

import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.model.entity.Order;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class OrderDTOConverter {

    private final ModelMapper modelMapper;

    public OrderDTOConverter(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public Order convertToEntity(Order orderRequestDTO) {
        Order order = modelMapper.map(orderRequestDTO, Order.class);
        return order;
    }

    public OrderResponseDTO convertToDto(Order order) {
        OrderResponseDTO orderResponseDTO = modelMapper.map(order, OrderResponseDTO.class);
        return orderResponseDTO;
    }
}
