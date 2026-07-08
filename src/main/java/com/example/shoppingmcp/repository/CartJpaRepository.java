package com.example.shoppingmcp.repository;

import com.example.shoppingmcp.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartJpaRepository extends JpaRepository<CartEntity, String> {
}
