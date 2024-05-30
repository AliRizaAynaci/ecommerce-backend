package com.example.ecommerce.model.enums;

public enum OrderType {
    PENDING, // Sipariş alındı, ödeme bekleniyor
    PROCESSING, // Ödeme alındı, sipariş hazırlanıyor
    SHIPPED, // Sipariş kargoya verildi
    DELIVERED, // Sipariş teslim edildi
    CANCELLED // Sipariş iptal edildi
}
