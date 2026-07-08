package com.example.shoppingmcp.model;

import java.math.BigDecimal;

public record Product(
        String id,
        String name,
        String description,
        BigDecimal price,
        int stockQty,
        String category
) {}