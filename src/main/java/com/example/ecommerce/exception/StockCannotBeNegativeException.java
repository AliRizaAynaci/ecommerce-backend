package com.example.ecommerce.exception;

public class StockCannotBeNegativeException extends RuntimeException {

    public StockCannotBeNegativeException() {
        super("Product cannot be sold because there is not enough stock");
    }

}