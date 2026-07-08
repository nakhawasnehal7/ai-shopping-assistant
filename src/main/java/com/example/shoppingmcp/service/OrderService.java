package com.example.shoppingmcp.service;

import com.example.shoppingmcp.entity.OrderEntity;
import com.example.shoppingmcp.model.Order;
import com.example.shoppingmcp.model.Product;
import com.example.shoppingmcp.repository.OrderJpaRepository;
import com.example.shoppingmcp.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Order handling backed by a persistent H2 database, with Stripe payment
 * (test mode), coupon discounts, flat-rate tax, and refund support.
 */
@Service
public class OrderService {

    private final OrderJpaRepository orderJpaRepository;
    private final ProductRepository products;
    private final StripePaymentService paymentService;
    private final CouponService couponService;
    private final TaxService taxService;
    private final AtomicInteger sequence = new AtomicInteger(1);

    public OrderService(OrderJpaRepository orderJpaRepository, ProductRepository products,
                         StripePaymentService paymentService, CouponService couponService,
                         TaxService taxService) {
        this.orderJpaRepository = orderJpaRepository;
        this.products = products;
        this.paymentService = paymentService;
        this.couponService = couponService;
        this.taxService = taxService;
    }

    @Transactional
    public Order create(String productId, int quantity, String customerId, String couponCode) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        Product product = products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));

        boolean reserved = products.decrementStock(productId, quantity);
        if (!reserved) {
            throw new IllegalStateException(
                    "Insufficient stock for " + productId + " (requested " + quantity + ")");
        }

        BigDecimal subtotal = product.price().multiply(BigDecimal.valueOf(quantity));
        BigDecimal discount = couponService.applyDiscount(subtotal, couponCode);
        BigDecimal taxable = subtotal.subtract(discount);
        BigDecimal tax = taxService.calculateTax(taxable);
        BigDecimal total = taxable.add(tax);

        String orderId = "ORD-" + sequence.getAndIncrement();

        String paymentIntentId;
        try {
            paymentIntentId = paymentService.charge(total,
                    "Order " + orderId + ": " + quantity + "x " + product.name());
        } catch (Exception e) {
            releaseStock(productId, quantity);
            throw e;
        }

        OrderEntity entity = new OrderEntity(orderId, productId, quantity, "PAID",
                subtotal, discount, tax, total, paymentIntentId, customerId, couponCode);
        orderJpaRepository.save(entity);

        return toRecord(entity);
    }

    public Optional<Order> findById(String orderId) {
        return orderJpaRepository.findById(orderId).map(this::toRecord);
    }

    public List<Order> listOrders(String customerId) {
        List<OrderEntity> entities = (customerId == null || customerId.isBlank())
                ? orderJpaRepository.findAll()
                : orderJpaRepository.findByCustomerId(customerId);
        return entities.stream().map(this::toRecord).toList();
    }

    public List<OrderEntity> allOrderEntities() {
        return orderJpaRepository.findAll();
    }

    @Transactional
    public Order cancelOrder(String orderId) {
        OrderEntity order = orderJpaRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));

        if (!"PAID".equals(order.getStatus())) {
            throw new IllegalStateException(
                    "Order " + orderId + " cannot be cancelled from status " + order.getStatus());
        }

        String refundId = paymentService.refund(order.getStripePaymentIntentId());
        releaseStock(order.getProductId(), order.getQuantity());

        order.setStatus("REFUNDED");
        order.setStripeRefundId(refundId);
        orderJpaRepository.save(order);

        return toRecord(order);
    }

    @Transactional
    public void markPaymentSucceeded(String paymentIntentId) {
        orderJpaRepository.findByStripePaymentIntentId(paymentIntentId).ifPresent(order -> {
            if ("PENDING_PAYMENT".equals(order.getStatus())) {
                order.setStatus("PAID");
                orderJpaRepository.save(order);
            }
        });
    }

    @Transactional
    public void markPaymentFailed(String paymentIntentId) {
        orderJpaRepository.findByStripePaymentIntentId(paymentIntentId).ifPresent(order -> {
            if (!"PAYMENT_FAILED".equals(order.getStatus()) && !"REFUNDED".equals(order.getStatus())) {
                releaseStock(order.getProductId(), order.getQuantity());
                order.setStatus("PAYMENT_FAILED");
                orderJpaRepository.save(order);
            }
        });
    }

    private void releaseStock(String productId, int quantity) {
/*        products.findById(productId).ifPresent(p -> products.save(new Product(
                p.id(), p.name(), p.description(), p.price(), p.stockQty() + quantity, p.category())));
    */}

    private Order toRecord(OrderEntity e) {
        return new Order(e.getOrderId(), e.getProductId(), e.getQuantity(), e.getStatus(),
                e.getSubtotal(), e.getDiscount(), e.getTax(), e.getTotal(), e.getCustomerId(), e.getCouponCode());
    }
}
