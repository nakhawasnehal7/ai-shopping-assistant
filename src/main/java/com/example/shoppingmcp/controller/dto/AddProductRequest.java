package com.example.shoppingmcp.controller.dto;

import java.math.BigDecimal;

public record AddProductRequest(String name, String description, BigDecimal price, int stockQty, String category) {}
