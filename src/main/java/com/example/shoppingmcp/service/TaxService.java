package com.example.shoppingmcp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Simplified flat-rate tax calculation. Real tax calculation (nexus rules,
 * per-jurisdiction rates, exemptions) should use a dedicated service like
 * Stripe Tax or Avalara — this is a placeholder that applies one configurable
 * flat percentage to every order regardless of location.
 */
@Service
public class TaxService {

    private final BigDecimal taxRatePercent;

    public TaxService(@Value("${shopping.tax-rate-percent:8.0}") double taxRatePercent) {
        this.taxRatePercent = BigDecimal.valueOf(taxRatePercent);
    }

    public BigDecimal calculateTax(BigDecimal taxableAmount) {
        return taxableAmount
                .multiply(taxRatePercent)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }
}
