package com.example.ecommerce.service;

import com.example.ecommerce.dto.converter.OrderDTOConverter;
import com.example.ecommerce.dto.response.OrderResponseDTO;
import com.example.ecommerce.exception.CartNotFoundException;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.OrderNotFoundException;
import com.example.ecommerce.exception.StockNotEnough;
import com.example.ecommerce.model.entity.*;
import com.example.ecommerce.model.enums.OrderType;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.OrderRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.impl.OrderServiceImpl;
import com.example.ecommerce.service.interfaces.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

public class OrderServiceImplTest {

    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderDTOConverter orderDTOConverter;
    @Mock
    private CartService cartService;

    private Customer customer;
    private Cart cart;
    private Product product;
    private Order order;
    private OrderResponseDTO orderResponseDTO;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        customer.setId(1L);

        cart = new Cart();
        cart.setId(1L);
        cart.setCustomer(customer);
        cart.setTotalPrice(BigDecimal.valueOf(100));

        product = new Product();
        product.setId(1L);
        product.setStock(5);
        product.setPrice(BigDecimal.TEN);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cart.setCartItems(new ArrayList<>(List.of(cartItem)));

        order = new Order();
        order.setId(1L);
        order.setCustomer(customer);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderType.PENDING);
        order.setTotalPrice(BigDecimal.valueOf(100));
        order.setOrderCode("order123");

        orderResponseDTO = new OrderResponseDTO();
        orderResponseDTO.setId(1L);
    }

    @Test
    void placeOrder_successfulOrder() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any())).thenReturn(order);
        when(orderDTOConverter.convertToDto(order)).thenReturn(orderResponseDTO);

        OrderResponseDTO result = orderService.placeOrder(1L);

        assertNotNull(result);
        verify(cartService).emptyCart(1L);
        verify(productRepository).save(product);
    }

    @Test
    void placeOrder_cartNotFound_throwsCartNotFoundException() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CartNotFoundException.class, () -> orderService.placeOrder(1L));
    }

    @Test
    void placeOrder_customerNotFound_throwsCustomerNotFoundException() {
        cart.setCustomer(null);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        assertThrows(CustomerNotFoundException.class, () -> orderService.placeOrder(1L));
    }

    @Test
    void placeOrder_stockNotEnough_throwsStockNotEnoughException() {
        product.setStock(1);
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        assertThrows(StockNotEnough.class, () -> orderService.placeOrder(1L));
    }

    @Test
    void placeOrder_cartIsEmpty_throwsRuntimeException() {
        cart.setCartItems(new ArrayList<>());
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        assertThrows(RuntimeException.class, () -> orderService.placeOrder(1L));

        verify(productRepository, never()).save(any());

        verify(cartService, never()).emptyCart(anyLong());
    }

    @Test
    void placeOrder_multipleProducts_orderCreatedSuccessfully() {
        Product product2 = new Product();
        product2.setId(2L);
        product2.setStock(3);
        product2.setPrice(BigDecimal.valueOf(15));

        CartItem cartItem2 = new CartItem();
        cartItem2.setProduct(product2);
        cartItem2.setQuantity(1);
        cart.getCartItems().add(cartItem2);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(orderRepository.save(any())).thenReturn(order);
        when(orderDTOConverter.convertToDto(order)).thenReturn(orderResponseDTO);

        OrderResponseDTO result = orderService.placeOrder(1L);

        assertNotNull(result);
        verify(cartService).emptyCart(1L);

        verify(productRepository).save(product);
        verify(productRepository).save(product2);
    }

    @Test
    void getorderForCode_orderExists_returnsOrderResponseDTO() {
        when(orderRepository.findByOrderCode("order123")).thenReturn(Optional.of(order));
        when(orderDTOConverter.convertToDto(order)).thenReturn(orderResponseDTO);

        OrderResponseDTO result = orderService.getorderForCode("order123");

        assertNotNull(result);
        assertEquals(orderResponseDTO, result);
    }

    @Test
    void getorderForCode_orderNotFound_throwsOrderNotFoundException() {
        when(orderRepository.findByOrderCode("order123")).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.getorderForCode("order123"));
    }

    @Test
    void getAllOrdersForCustomer_customerExists_returnsListOfOrderResponseDTO() {
        List<Order> orders = new ArrayList<>();
        orders.add(order);
        customer.setOrders(orders);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(orderDTOConverter.convertToDto(order)).thenReturn(orderResponseDTO);
        List<OrderResponseDTO> orderResponseDTOList = new ArrayList<>();
        orderResponseDTOList.add(orderResponseDTO);

        List<OrderResponseDTO> result = orderService.getAllOrdersForCustomer(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orderResponseDTOList, result);
    }

    @Test
    void getAllOrdersForCustomer_customerNotFound_throwsCustomerNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> orderService.getAllOrdersForCustomer(1L));
    }

}
