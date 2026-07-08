package com.example.shoppingmcp.model;

import java.math.BigDecimal;
import java.util.List;

public record SalesSummary(
        long totalOrders,
        BigDecimal totalRevenue,
        List<ProductSalesCount> topProducts,
        List<Product> lowStockProducts
) {
    public record ProductSalesCount(String productId, int unitsSold) {}
}
