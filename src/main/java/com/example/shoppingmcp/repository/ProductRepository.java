package com.example.shoppingmcp.repository;

import com.example.shoppingmcp.entity.ProductEntity;
import com.example.shoppingmcp.model.Product;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Product catalog backed by a persistent H2 database via Spring Data JPA.
 * Seeds sample data on first run only (won't duplicate on restart).
 */
@Repository
public class ProductRepository {

    private final ProductJpaRepository jpaRepository;
    private final AtomicInteger sequence = new AtomicInteger(1007);

    public ProductRepository(ProductJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @PostConstruct
    public void seedIfEmpty() {
        if (jpaRepository.count() > 0) {
            // Already seeded on a previous run — resume the ID sequence past existing products
            jpaRepository.findAll().stream()
                    .map(ProductEntity::getId)
                    .filter(id -> id.startsWith("P-"))
                    .map(id -> id.substring(2))
                    .filter(id -> id.chars().allMatch(Character::isDigit))
                    .mapToInt(Integer::parseInt)
                    .max()
                    .ifPresent(max -> sequence.set(max + 1));
            return;
        }

        save(new Product("P-1001", "Wireless Headphones",
                "Over-ear, active noise cancelling, 30h battery",
                new BigDecimal("129.99"), 42, "audio"));

        save(new Product("P-1002", "Wireless Earbuds",
                "In-ear, touch controls, wireless charging case",
                new BigDecimal("79.99"), 120, "audio"));

        save(new Product("P-1003", "Mechanical Keyboard",
                "Hot-swappable switches, RGB backlight, tenkeyless",
                new BigDecimal("109.00"), 15, "accessories"));

        save(new Product("P-1004", "USB-C Hub",
                "7-in-1, HDMI, 3x USB-A, SD card reader",
                new BigDecimal("34.50"), 200, "accessories"));

        save(new Product("P-1005", "Studio Monitor Speakers (Pair)",
                "Powered near-field monitors, 5-inch woofer",
                new BigDecimal("249.00"), 8, "audio"));

        save(new Product("P-1006", "27-inch 4K Monitor",
                "IPS panel, 60Hz, USB-C with 65W power delivery",
                new BigDecimal("329.00"), 25, "displays"));
    }

    public void save(Product product) {
/*        jpaRepository.save(new ProductEntity(product.id(), product.name(), product.description(),
                product.price(), product.stockQty(), product.category()));*/
    }

    public Optional<Product> findById(String id) {
        return jpaRepository.findById(id).map(this::toRecord);
    }

    /**
     * Case-insensitive keyword search across name and description,
     * with an optional category filter.
     */
    public List<Product> search(String query, String category) {
        String q = query == null ? "" : query.toLowerCase();

        return jpaRepository.findAll().stream()
                .filter(p -> q.isBlank()
                        || p.getName().toLowerCase().contains(q)
                        || p.getDescription().toLowerCase().contains(q))
                .filter(p -> category == null || category.isBlank()
                        || p.getCategory().equalsIgnoreCase(category))
                .map(this::toRecord)
                .toList();
    }

    public List<Product> findAll() {
        return jpaRepository.findAll().stream().map(this::toRecord).toList();
    }

    public boolean decrementStock(String productId, int quantity) {
        return jpaRepository.findById(productId)
                .filter(p -> p.getStockQty() >= quantity)
                .map(p -> {
                    p.setStockQty(p.getStockQty() - quantity);
                    jpaRepository.save(p);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Creates a new product with a generated ID and adds it to the catalog.
     */
    public Product addProduct(String name, String description, BigDecimal price, int stockQty, String category) {
        String id = "P-" + sequence.getAndIncrement();
        Product product = new Product(id, name, description, price, stockQty, category);
        save(product);
        return product;
    }

    private Product toRecord(ProductEntity e) {
        return new Product(e.getId(), e.getName(), e.getDescription(), e.getPrice(), e.getStockQty(), e.getCategory());
    }
}
