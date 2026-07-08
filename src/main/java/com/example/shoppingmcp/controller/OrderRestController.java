package com.example.shoppingmcp.controller;

import com.example.shoppingmcp.controller.dto.PlaceOrderRequest;
import com.example.shoppingmcp.model.Order;
import com.example.shoppingmcp.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderRestController {

    private final OrderService orders;

    public OrderRestController(OrderService orders) {
        this.orders = orders;
    }

    @PostMapping
    public ResponseEntity<Order> placeOrder(@RequestBody PlaceOrderRequest request) {
        Order order = orders.create(request.productId(), request.quantity(), request.customerId(), request.couponCode());
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    @GetMapping("/{orderId}")
    public Order getOrder(@PathVariable String orderId) {
        return orders.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    @GetMapping
    public List<Order> listOrders(@RequestParam(required = false) String customerId) {
        return orders.listOrders(customerId);
    }

    @PostMapping("/{orderId}/cancel")
    public Order cancelOrder(@PathVariable String orderId) {
        return orders.cancelOrder(orderId);
    }
}
