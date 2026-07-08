package com.example.shoppingmcp.controller;

import com.example.shoppingmcp.controller.dto.AddToCartRequest;
import com.example.shoppingmcp.controller.dto.CheckoutRequest;
import com.example.shoppingmcp.model.Order;
import com.example.shoppingmcp.service.CartService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart/{customerId}")
public class CartRestController {

    private final CartService cartService;

    public CartRestController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public Map<String, Integer> viewCart(@PathVariable String customerId) {
        return cartService.viewCart(customerId);
    }

    @PostMapping("/items")
    public Map<String, Integer> addItem(@PathVariable String customerId, @RequestBody AddToCartRequest request) {
        return cartService.addItem(customerId, request.productId(), request.quantity());
    }

    @DeleteMapping("/items/{productId}")
    public Map<String, Integer> removeItem(@PathVariable String customerId, @PathVariable String productId) {
        return cartService.removeItem(customerId, productId);
    }

    @PostMapping("/checkout")
    public List<Order> checkout(@PathVariable String customerId, @RequestBody(required = false) CheckoutRequest request) {
        String couponCode = request != null ? request.couponCode() : null;
        return cartService.checkout(customerId, couponCode);
    }
}
