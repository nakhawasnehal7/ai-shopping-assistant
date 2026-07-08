package com.example.shoppingmcp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String productId;
    private String customerId;
    private int rating;
    private String comment;

    protected ReviewEntity() {}

    public ReviewEntity(String productId, String customerId, int rating, String comment) {
        this.productId = productId;
        this.customerId = customerId;
        this.rating = rating;
        this.comment = comment;
    }

    public Long getId() { return id; }
    public String getProductId() { return productId; }
    public String getCustomerId() { return customerId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
}
