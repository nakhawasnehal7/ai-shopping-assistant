package com.example.shoppingmcp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class CouponEntity {

    @Id
    private String code;
    private int discountPercent;
    private boolean active;

    protected CouponEntity() {}

    public CouponEntity(String code, int discountPercent, boolean active) {
        this.code = code;
        this.discountPercent = discountPercent;
        this.active = active;
    }

    public String getCode() { return code; }
    public int getDiscountPercent() { return discountPercent; }
    public boolean isActive() { return active; }
}
