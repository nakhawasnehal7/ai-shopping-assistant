package com.example.shoppingmcp.service;

import com.example.shoppingmcp.entity.CouponEntity;
import com.example.shoppingmcp.repository.CouponJpaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class CouponService {

    private final CouponJpaRepository repository;

    public CouponService(CouponJpaRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void seedIfEmpty() {
        if (repository.count() > 0) {
            return;
        }
        repository.save(new CouponEntity("WELCOME10", 10, true));
        repository.save(new CouponEntity("SAVE20", 20, true));
    }

    /**
     * Returns the discount percentage for a valid, active coupon, or empty if
     * the code doesn't exist or is inactive.
     */
    public Optional<Integer> validate(String code) {
        if (code == null || code.isBlank()) {
            return Optional.empty();
        }
        return repository.findById(code.toUpperCase())
                .filter(CouponEntity::isActive)
                .map(CouponEntity::getDiscountPercent);
    }

    public BigDecimal applyDiscount(BigDecimal subtotal, String code) {
        return validate(code)
                .map(pct -> subtotal.multiply(BigDecimal.valueOf(pct)).divide(BigDecimal.valueOf(100)))
                .orElse(BigDecimal.ZERO);
    }

    public CouponEntity createCoupon(String code, int discountPercent) {
        CouponEntity coupon = new CouponEntity(code.toUpperCase(), discountPercent, true);
        return repository.save(coupon);
    }
}
