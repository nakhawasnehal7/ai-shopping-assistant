package com.example.shoppingmcp.model;

public record Review(
        Long id,
        String productId,
        String customerId,
        int rating,
        String comment
) {}
