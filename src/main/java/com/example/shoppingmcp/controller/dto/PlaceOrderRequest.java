package com.example.shoppingmcp.controller.dto;

public record PlaceOrderRequest(String productId, int quantity, String customerId, String couponCode) {}
