package com.example.shoppingmcp.service;

import com.example.shoppingmcp.entity.CartEntity;
import com.example.shoppingmcp.model.Order;
import com.example.shoppingmcp.repository.CartJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private final CartJpaRepository cartRepository;
    private final OrderService orderService;

    public CartService(CartJpaRepository cartRepository, OrderService orderService) {
        this.cartRepository = cartRepository;
        this.orderService = orderService;
    }

    @Transactional
    public Map<String, Integer> addItem(String customerId, String productId, int quantity) {
        CartEntity cart = getOrCreateCart(customerId);
        cart.getItems().merge(productId, quantity, Integer::sum);
        cartRepository.save(cart);
        return cart.getItems();
    }

    @Transactional
    public Map<String, Integer> removeItem(String customerId, String productId) {
        CartEntity cart = getOrCreateCart(customerId);
        cart.getItems().remove(productId);
        cartRepository.save(cart);
        return cart.getItems();
    }

    public Map<String, Integer> viewCart(String customerId) {
        return getOrCreateCart(customerId).getItems();
    }

    /**
     * Checks out every item in the cart. Since orders are single-product,
     * this places one order (and one Stripe charge) per distinct product in
     * the cart, then clears it. Returns all orders placed. If any item fails
     * (e.g. out of stock), earlier successfully placed orders in this
     * checkout are NOT automatically rolled back — each is a separate charge.
     */
    @Transactional
    public List<Order> checkout(String customerId, String couponCode) {
        CartEntity cart = getOrCreateCart(customerId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty for customer: " + customerId);
        }

        List<Order> placedOrders = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : cart.getItems().entrySet()) {
            Order order = orderService.create(entry.getKey(), entry.getValue(), customerId, couponCode);
            placedOrders.add(order);
        }

        cart.getItems().clear();
        cartRepository.save(cart);

        return placedOrders;
    }

    private CartEntity getOrCreateCart(String customerId) {
        return cartRepository.findById(customerId)
                .orElseGet(() -> new CartEntity(customerId));
    }
}
