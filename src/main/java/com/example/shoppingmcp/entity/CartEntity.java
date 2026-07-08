package com.example.shoppingmcp.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyColumn;

import java.util.HashMap;
import java.util.Map;

/**
 * One cart per customer. Stores productId -> quantity. Simplified design:
 * checkout creates one Order per line item (reusing the existing
 * single-product order/payment flow) rather than introducing a multi-line
 * order model.
 */
@Entity
public class CartEntity {

    @Id
    private String customerId;

    @ElementCollection
    @CollectionTable(name = "cart_items", joinColumns = @JoinColumn(name = "customer_id"))
    @MapKeyColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<String, Integer> items = new HashMap<>();

    protected CartEntity() {}

    public CartEntity(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerId() { return customerId; }
    public Map<String, Integer> getItems() { return items; }
}
