package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.service.interfaces.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PutMapping("/{cartId}/{productId}/{quantity}")
    public ResponseEntity<OrderResponseDTO> placeOrder(@PathVariable Long cartId) {
        OrderResponseDTO orderDTO = orderService.placeOrder(cartId);
        return ResponseEntity.ok(orderDTO);
    }


    @GetMapping("/{orderCode}")
    public ResponseEntity<OrderResponseDTO> getOrder(@PathVariable String orderCode) {
        OrderResponseDTO orderDTO = orderService.getorderForCode(orderCode);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderResponseDTO>> getOrderByCustomerId(@PathVariable Long customerId) {
        List<OrderResponseDTO> orderDTO = orderService.getAllOrdersForCustomer(customerId);
        return ResponseEntity.ok(orderDTO);
    }

}
