package com.example.shoppingmcp.controller;

import com.example.shoppingmcp.model.SalesSummary;
import com.example.shoppingmcp.service.AnalyticsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AnalyticsRestController {

    private final AnalyticsService analyticsService;

    public AnalyticsRestController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/sales-summary")
    public SalesSummary getSalesSummary() {
        return analyticsService.getSalesSummary();
    }
}
