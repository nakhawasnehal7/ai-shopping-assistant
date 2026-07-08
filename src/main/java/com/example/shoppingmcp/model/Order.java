package com.example.shoppingmcp.model;

import java.math.BigDecimal;

public record Order(
        String orderId,
        String productId,
        int quantity,
        String status,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal tax,
        BigDecimal total,
        String customerId,
        String couponCode
) {}
