package com.example.shoppingmcp.repository;

import com.example.shoppingmcp.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, String> {

    Optional<OrderEntity> findByStripePaymentIntentId(String stripePaymentIntentId);

    List<OrderEntity> findByCustomerId(String customerId);
}
