package com.example.shoppingmcp.repository;

import com.example.shoppingmcp.entity.CouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponJpaRepository extends JpaRepository<CouponEntity, String> {
}
