package com.example.shoppingmcp.controller.dto;

public record AddReviewRequest(String customerId, int rating, String comment) {}
