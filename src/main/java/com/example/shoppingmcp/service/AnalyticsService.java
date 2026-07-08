package com.example.shoppingmcp.service;

import com.example.shoppingmcp.entity.OrderEntity;
import com.example.shoppingmcp.model.Product;
import com.example.shoppingmcp.model.SalesSummary;
import com.example.shoppingmcp.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private static final int LOW_STOCK_THRESHOLD = 10;

    private final OrderService orderService;
    private final ProductRepository products;

    public AnalyticsService(OrderService orderService, ProductRepository products) {
        this.orderService = orderService;
        this.products = products;
    }

    public SalesSummary getSalesSummary() {
        List<OrderEntity> paidOrders = orderService.allOrderEntities().stream()
                .filter(o -> "PAID".equals(o.getStatus()))
                .toList();

        BigDecimal totalRevenue = paidOrders.stream()
                .map(OrderEntity::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Integer> unitsByProduct = paidOrders.stream()
                .collect(Collectors.groupingBy(OrderEntity::getProductId,
                        Collectors.summingInt(OrderEntity::getQuantity)));

        List<SalesSummary.ProductSalesCount> topProducts = unitsByProduct.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed())
                .limit(5)
                .map(e -> new SalesSummary.ProductSalesCount(e.getKey(), e.getValue()))
                .toList();

        List<Product> lowStock = products.findAll().stream()
                .filter(p -> p.stockQty() < LOW_STOCK_THRESHOLD)
                .toList();

        return new SalesSummary(paidOrders.size(), totalRevenue, topProducts, lowStock);
    }
}
