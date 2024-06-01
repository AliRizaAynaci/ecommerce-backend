package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.converter.OrderDTOConverter;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.exception.CartNotFoundException;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.OrderNotFoundException;
import com.example.ecommerce.model.entity.*;
import com.example.ecommerce.model.enums.OrderType;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.interfaces.CartService;
import com.example.ecommerce.service.interfaces.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderDTOConverter orderDTOConverter;
    private final CartService cartService;

    public OrderServiceImpl(OrderRepository orderRepository, CartRepository cartRepository,
                            CustomerRepository customerRepository, ProductRepository productRepository,
                            OrderDTOConverter orderDTOConverter, CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.orderDTOConverter = orderDTOConverter;
        this.cartService = cartService;
    }

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found"));

        Customer customer = cart.getCustomer();
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderType.PENDING);
        order.setTotalPrice(cart.getTotalPrice());

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cart.getCartItems()) {
            Product product = cartItem.getProduct();

            if (cartItem.getQuantity() > product.getStock()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPriceAtPurchase(product.getPrice());
            orderItems.add(orderItem);

            productRepository.save(product);
        }

        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);

        cartService.emptyCart(cartId);

        return orderDTOConverter.convertToDto(savedOrder);
    }


    @Override
    public OrderResponseDTO getorderForCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new OrderNotFoundException("Order not found with order code: " + orderCode));
        return orderDTOConverter.convertToDto(order);
    }

    @Override
    public List<OrderResponseDTO> getAllOrdersForCustomer(Long customerId) {
        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer == null) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }
        List<Order> orders = customer.get().getOrders();
        return orders.stream()
                .map(orderDTOConverter::convertToDto)
                .collect(Collectors.toList());
    }
}
