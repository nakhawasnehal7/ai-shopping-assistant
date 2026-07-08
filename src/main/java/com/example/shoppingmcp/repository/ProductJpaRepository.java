package com.example.shoppingmcp.repository;

import com.example.shoppingmcp.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, String> {
}
