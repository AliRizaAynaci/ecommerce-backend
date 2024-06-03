package com.example.ecommerce.service;


import com.example.ecommerce.dto.converter.CartDTOConverter;
import com.example.ecommerce.dto.converter.CartItemDtoConverter;
import com.example.ecommerce.dto.converter.ProductDTOConverter;
import com.example.ecommerce.dto.response.CartDTO;
import com.example.ecommerce.dto.response.CartItemDTO;
import com.example.ecommerce.exception.CartNotFoundException;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.exception.StockNotEnough;
import com.example.ecommerce.model.entity.Cart;
import com.example.ecommerce.model.entity.CartItem;
import com.example.ecommerce.model.entity.Customer;
import com.example.ecommerce.model.entity.Product;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.impl.CartServiceImpl;
import com.example.ecommerce.service.interfaces.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private CartDTOConverter cartDTOConverter;

    @Mock
    private ProductDTOConverter productDTOConverter;

    @Mock
    private CartItemDtoConverter cartItemDtoConverter;

    @InjectMocks
    private CartServiceImpl cartService;

    private Customer customer;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        customer = new Customer();
        cart = new Cart();
        product = new Product();
    }

    @Test
    void getCart_cartExists_returnCartDTO() {
        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        CartDTO cartDTO = new CartDTO();
        when(cartDTOConverter.convertToDto(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.getCart(1L);

        assertNotNull(result);
        assertEquals(cartDTO, result);
    }

    @Test
    void getCart_cartNotFound_throwCartNotFoundException() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.getCart(1L));
    }

    @Test
    void getCartByCustomerId_customerAndCartExist_returnCartDTO() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        customer.setCart(cart);
        CartDTO cartDTO = new CartDTO();
        when(cartDTOConverter.convertToDto(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.getCartByCustomerId(1L);

        assertNotNull(result);
        assertEquals(cartDTO, result);
    }

    @Test
    void getCartByCustomerId_customerNotFound_throwCustomerNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> cartService.getCartByCustomerId(1L));
    }

    @Test
    void getCartByCustomerId_cartNotFound_throwCartNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        customer.setCart(null);

        assertThrows(CartNotFoundException.class, () -> cartService.getCartByCustomerId(1L));
    }

    @Test
    void addItemToCart_productExists_quantityUpdated_returnCartDTO() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        product.setStock(10);
        product.setPrice(BigDecimal.valueOf(100.00));
        customer.setCart(cart);

        // Initialize the cartItems list
        cart.setCartItems(new ArrayList<>());

        CartDTO cartDTO = new CartDTO();
        when(cartDTOConverter.convertToDto(cart)).thenReturn(cartDTO);
        CartItemDTO cartItemDTO = new CartItemDTO();
        when(cartItemDtoConverter.convertToDto(any(CartItem.class))).thenReturn(cartItemDTO);

        CartDTO result = cartService.addItemToCart(1L, 1L, 2);

        assertNotNull(result);
        assertEquals(cartDTO, result);
        assertEquals(1, cart.getCartItems().size());
    }

    @Test
    void addItemToCart_customerNotFound_throwCustomerNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CustomerNotFoundException.class, () -> cartService.addItemToCart(1L, 1L, 2));
    }

    @Test
    void addItemToCart_productNotFound_throwProductNotFoundException() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> cartService.addItemToCart(1L, 1L, 2));
    }

    @Test
    void addItemToCart_stockNotEnough_throwStockNotEnough() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        product.setStock(1);
        customer.setCart(cart);

        cart.setCartItems(new ArrayList<>());

        assertThrows(StockNotEnough.class, () -> cartService.addItemToCart(1L, 1L, 2));
    }

    @Test
    void updateCartItem_cartAndProductExist_quantityUpdated() {
        Product product = new Product();
        product.setId(1L); // Ensure product ID is set

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartRepository.save(cart)).thenReturn(cart);
        CartDTO cartDTO = new CartDTO();
        when(cartDTOConverter.convertToDto(cart)).thenReturn(cartDTO);

        CartDTO result = cartService.updateCartItem(1L, 1L, 5);

        assertNotNull(result);
        assertEquals(cartDTO, result);
        assertEquals(5, cartItem.getQuantity());
    }

    @Test
    void updateCartItem_cartNotFound_throwCartNotFoundException() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.updateCartItem(1L, 1L, 5));
    }

    @Test
    void updateCartItem_productNotFound_throwProductNotFoundException() {
        List<CartItem> cartItems = new ArrayList<>();
        cart.setCartItems(cartItems);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        assertThrows(ProductNotFoundException.class, () -> cartService.updateCartItem(1L, 1L, 5));
    }

    @Test
    void removeItemFromCart_cartAndProductExist_itemRemoved() {
        Product product = new Product();
        product.setId(1L);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        List<CartItem> cartItems = new ArrayList<>();
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartService.removeItemFromCart(1L, 1L);

        assertEquals(0, cart.getCartItems().size());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void removeItemFromCart_cartNotFound_throwCartNotFoundException() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.removeItemFromCart(1L, 1L));
    }

    @Test
    void emptyCart_cartExists_cartEmptied() {
        List<CartItem> cartItems = new ArrayList<>();
        CartItem cartItem = new CartItem();
        cartItem.setProduct(new Product());
        cartItems.add(cartItem);
        cart.setCartItems(cartItems);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));

        cartService.emptyCart(1L);

        assertEquals(0, cart.getCartItems().size());
        assertEquals(BigDecimal.ZERO, cart.getTotalPrice());
        verify(cartRepository, times(1)).save(cart);
    }

    @Test
    void emptyCart_cartNotFound_throwCartNotFoundException() {
        when(cartRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> cartService.emptyCart(1L));
    }
}
