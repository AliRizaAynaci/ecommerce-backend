package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.converter.CartDTOConverter;
import com.example.ecommerce.dto.converter.CartItemDtoConverter;
import com.example.ecommerce.dto.converter.ProductDTOConverter;
import com.example.ecommerce.dto.request.ProductRequestDTO;
import com.example.ecommerce.dto.response.CartDTO;
import com.example.ecommerce.dto.response.CartItemDTO;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final CartDTOConverter cartDTOConverter;
    private final ProductDTOConverter productDTOConverter;
    private final CartItemDtoConverter cartItemDtoConverter;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository,
                           CustomerRepository customerRepository, CartDTOConverter cartDTOConverter,
                           ProductDTOConverter productDTOConverter, CartItemDtoConverter cartItemDtoConverter) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.cartDTOConverter = cartDTOConverter;
        this.productDTOConverter = productDTOConverter;
        this.cartItemDtoConverter = cartItemDtoConverter;
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
            cart = new Cart();
            cart.setCustomer(customer);
            cart.setCartItems(new ArrayList<>());
            cart = cartRepository.save(cart);
            customer.setCart(cart);
            customerRepository.save(customer);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId)); // Ensure product exists

        Optional<CartItem> existingCartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        CartItem cartItem;
        if (existingCartItem.isPresent()) {
            cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + 1); // Increment quantity
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product); // Set the product for the new cart item
            cartItem.setQuantity(1); // Initial quantity is 1
            cart.getCartItems().add(cartItem);
        }

        recalculateTotalPrice(cart); // Recalculate total after adding or updating

        cartRepository.save(cart);

        // Create CartItemDTO and set productRequestDTO
        CartItemDTO cartItemDTO = cartItemDtoConverter.convertToDto(cartItem);
        cartItemDTO.setProduct(product);

        CartDTO cartDTO = cartDTOConverter.convertToDto(cart);
        cartDTO.getCartItems().add(cartItemDTO);
        return cartDTO;
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

    private void recalculateTotalPrice(Cart cart) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct() == null || item.getQuantity() == 0) {
                continue;
            }

            BigDecimal itemPrice = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);
        }
        cart.setTotalPrice(totalPrice);
    }
}
