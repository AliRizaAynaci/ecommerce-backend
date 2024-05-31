package com.example.ecommerce.service.impl;

import com.example.ecommerce.dto.converter.CartDTOConverter;
import com.example.ecommerce.dto.converter.CartItemDtoConverter;
import com.example.ecommerce.dto.converter.ProductDTOConverter;
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
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
            cart = createCart(customer);
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));


        boolean itemExists = cart.getCartItems().stream()
                .anyMatch(item -> item.getProduct() != null && item.getProduct().getId().equals(productId));

        CartItem cartItem;
        if (!itemExists) {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(1);
            cart.getCartItems().add(cartItem);
        } else {
            CartItem existingItem = cart.getCartItems().stream()
                    .filter(item -> item.getProduct() != null && item.getProduct().getId().equals(productId))
                    .findFirst()
                    .get();
            existingItem.setQuantity(existingItem.getQuantity() + 1);
        }

        recalculateTotalPrice(cart); // Toplam fiyatı yeniden hesapla

        cartRepository.save(cart);

        CartDTO cartDTO = cartDTOConverter.convertToDto(cart);
        List<CartItemDTO> filteredCartItems = cart.getCartItems().stream()
                .filter(item -> item.getProduct() != null)
                .map(cartItemDtoConverter::convertToDto)
                .collect(Collectors.toList());
        cartDTO.setCartItems(filteredCartItems);

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
        // totalPrice'ı 0 olarak başlat
        BigDecimal totalPrice = BigDecimal.ZERO; // Başlangıç değerini sıfır yapın.

        for (CartItem item : cart.getCartItems()) {
            if (item.getProduct() == null || item.getQuantity() == 0) {
                continue; // Null veya 0 quantity olan öğeleri atla
            }

            BigDecimal itemPrice = item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            totalPrice = totalPrice.add(itemPrice);
        }

        cart.setTotalPrice(totalPrice); // Hesaplanan toplam fiyatı setetle
    }


    private Cart createCart(Customer customer) {
        Cart cart = new Cart();
        cart.setCustomer(customer);
        cart.setCartItems(new ArrayList<>());
        cart = cartRepository.save(cart);
        customer.setCart(cart);
        customerRepository.save(customer);
        return cart;
    }
}
