package com.example.ecommerce.exception;

public class StockNotEnough extends RuntimeException {

    public StockNotEnough() {
        super("Product cannot be sold because there is not enough stock");
    }

}