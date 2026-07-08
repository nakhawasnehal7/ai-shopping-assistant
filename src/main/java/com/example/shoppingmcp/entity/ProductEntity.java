package com.example.shoppingmcp.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.math.BigDecimal;

@Entity
public class ProductEntity {

    @Id
    private String id;
    private String name;
    private String description;
    private BigDecimal price;
    private int stockQty;
    private String category;

    protected ProductEntity() {
        // required by JPA
    }

    public ProductEntity(String id, String name, String description, BigDecimal price, int stockQty, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQty = stockQty;
        this.category = category;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public BigDecimal getPrice() { return price; }
    public int getStockQty() { return stockQty; }
    public String getCategory() { return category; }

    public void setStockQty(int stockQty) { this.stockQty = stockQty; }
}
