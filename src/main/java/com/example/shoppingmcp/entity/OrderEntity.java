package com.example.shoppingmcp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class OrderEntity {

    @Id
    private String orderId;
    private String productId;
    private int quantity;
    private String status;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal tax;
    private BigDecimal total;
    private String stripePaymentIntentId;
    private String stripeRefundId;
    private String customerId;
    private String couponCode;

    protected OrderEntity() {}

    public OrderEntity(String orderId, String productId, int quantity, String status,
                        BigDecimal subtotal, BigDecimal discount, BigDecimal tax, BigDecimal total,
                        String stripePaymentIntentId, String customerId, String couponCode) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.status = status;
        this.subtotal = subtotal;
        this.discount = discount;
        this.tax = tax;
        this.total = total;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.customerId = customerId;
        this.couponCode = couponCode;
    }

    public String getOrderId() { return orderId; }
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public String getStatus() { return status; }
    public BigDecimal getSubtotal() { return subtotal; }
    public BigDecimal getDiscount() { return discount; }
    public BigDecimal getTax() { return tax; }
    public BigDecimal getTotal() { return total; }
    public String getStripePaymentIntentId() { return stripePaymentIntentId; }
    public String getStripeRefundId() { return stripeRefundId; }
    public String getCustomerId() { return customerId; }
    public String getCouponCode() { return couponCode; }

    public void setStatus(String status) { this.status = status; }
    public void setStripeRefundId(String stripeRefundId) { this.stripeRefundId = stripeRefundId; }
}
