package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.converter.CartDTOConverter;
import com.example.ecommerce.dto.response.CartDTO;
import com.example.ecommerce.exception.CartNotFoundException;
import com.example.ecommerce.exception.CustomerNotFoundException;
import com.example.ecommerce.exception.ProductNotFoundException;
import com.example.ecommerce.model.entity.Cart;
import com.example.ecommerce.model.entity.CartItem;
import com.example.ecommerce.model.entity.Customer;
import com.example.ecommerce.model.entity.Product;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.CustomerRepository;
import com.example.ecommerce.repository.ProductRepository;
import com.example.ecommerce.service.interfaces.CartService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CartDTOConverter cartDTOConverter;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, CustomerRepository customerRepository, CartDTOConverter cartDTOConverter) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.cartDTOConverter = cartDTOConverter;
    }

    @Override
    public CartDTO getCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + cartId));
        return cartDTOConverter.convertToDto(cart);
    }

    @Override
    public CartDTO getCartByCustomerId(Long customerId) {

        Optional<Customer> customer = customerRepository.findById(customerId);
        if (customer.isEmpty()) {
            throw new CustomerNotFoundException("Customer not found with id: " + customerId);
        }

        Cart cart = customer.get().getCart();
        if (cart == null) {
            throw new CartNotFoundException("Cart not found for customer with id: " + customerId);
        }
        return cartDTOConverter.convertToDto(cart);

    }

    @Override
    public CartDTO addItemToCart(Long customerId, Long productId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("Customer not found with id: " + customerId));

        Cart cart = customer.getCart();
        if (cart == null) {
            Cart newCart = new Cart();
            newCart.setCustomer(customer);
            newCart.setCartItems(new ArrayList<>());
            cart = cartRepository.save(newCart);
            customer.setCart(cart);
            customerRepository.save(customer);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cart.getCartItems().add(cartItem);
        }
        cartRepository.save(cart);
        return cartDTOConverter.convertToDto(cart);
    }

    @Override
    public CartDTO updateCartItem(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + cartId));

        Optional<CartItem> cartItemOptional = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (cartItemOptional.isPresent()) {
            CartItem cartItem = cartItemOptional.get();
            cartItem.setQuantity(quantity);
            return cartDTOConverter.convertToDto(cartRepository.save(cart));
        } else {
            throw new ProductNotFoundException(productId);
        }
    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + cartId));

        cart.getCartItems().removeIf(item -> item.getProduct().getId().equals(productId));
        cartRepository.save(cart);
    }

    @Override
    public void emptyCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new CartNotFoundException("Cart not found with id: " + cartId));
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }
}
