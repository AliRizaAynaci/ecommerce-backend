package com.example.ecommerce.controller;

import com.example.ecommerce.dto.response.CartDTO;
import com.example.ecommerce.model.entity.Cart;
import com.example.ecommerce.service.interfaces.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDTO> getCart(@PathVariable Long cartId) {
        CartDTO cartDTO = cartService.getCart(cartId);
        return ResponseEntity.ok(cartDTO);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<CartDTO> getCartByCustomerId(@PathVariable Long customerId) {
        CartDTO cartDTO = cartService.getCartByCustomerId(customerId);
        return ResponseEntity.ok(cartDTO);
    }

    @PostMapping("/add-item/{customerId}/{productId}")
    public ResponseEntity<CartDTO> addItemToCart(@PathVariable Long customerId, @PathVariable Long productId,
                                                 @RequestParam(defaultValue = "1") int quantity) {
        CartDTO cartDTO = cartService.addItemToCart(customerId, productId, quantity);
        return ResponseEntity.ok(cartDTO);
    }

    @PutMapping("/{cartId}/{productId}/{quantity}")
    public ResponseEntity<CartDTO> updateCartItem(@PathVariable Long cartId, @PathVariable Long productId, @PathVariable int quantity) {
        CartDTO cartDTO = cartService.updateCartItem(cartId, productId, quantity);
        return ResponseEntity.ok(cartDTO);
    }

    @DeleteMapping("/{cartId}/{productId}")
    public ResponseEntity<Void> removeItemFromCart(@PathVariable Long cartId, @PathVariable Long productId) {
        cartService.removeItemFromCart(cartId, productId);
        return ResponseEntity.noContent().build();
    }


}
