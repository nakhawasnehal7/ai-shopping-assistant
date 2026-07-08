package com.example.shoppingmcp.repository;

import com.example.shoppingmcp.entity.ReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewJpaRepository extends JpaRepository<ReviewEntity, Long> {
    List<ReviewEntity> findByProductId(String productId);
}
