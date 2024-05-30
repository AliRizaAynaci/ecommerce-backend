package com.example.ecommerce.service.interfaces;

import com.example.ecommerce.dto.response.CartDTO;

public interface CartService {

    CartDTO getCart(Long cartId);
    CartDTO getCartByCustomerId(Long customerId);
    CartDTO addItemToCart(Long customerId, Long productId);
    CartDTO updateCartItem(Long cartId, Long productId, int quantity); // Güncelleme metodu
    void removeItemFromCart(Long cartId, Long productId);
    void emptyCart(Long cartId); // Boşaltma metodu
}
