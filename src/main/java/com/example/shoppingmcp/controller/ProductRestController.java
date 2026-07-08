package com.example.shoppingmcp.controller;

import com.example.shoppingmcp.controller.dto.AddProductRequest;
import com.example.shoppingmcp.model.Product;
import com.example.shoppingmcp.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductRestController {

    private final ProductRepository products;

    public ProductRestController(ProductRepository products) {
        this.products = products;
    }

    @GetMapping
    public List<Product> search(
            @RequestParam(required = false, defaultValue = "") String query,
            @RequestParam(required = false) String category) {
        return products.search(query, category);
    }

    @GetMapping("/{productId}")
    public Product getProduct(@PathVariable String productId) {
        return products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
    }

    @GetMapping("/{productId}/stock")
    public int getStock(@PathVariable String productId) {
/*
        return products.findById(productId).map(Product::stockQty).orElse(0);
*/

    return 0;
    }

    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody AddProductRequest request) {
        Product created = products.addProduct(
                request.name(), request.description(), request.price(), request.stockQty(), request.category());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
