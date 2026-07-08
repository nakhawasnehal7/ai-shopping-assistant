package com.example.shoppingmcp.service;

import com.example.shoppingmcp.entity.ReviewEntity;
import com.example.shoppingmcp.model.Review;
import com.example.shoppingmcp.repository.ReviewJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewService {

    private final ReviewJpaRepository repository;

    public ReviewService(ReviewJpaRepository repository) {
        this.repository = repository;
    }

    public Review addReview(String productId, String customerId, int rating, String comment) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        ReviewEntity saved = repository.save(new ReviewEntity(productId, customerId, rating, comment));
        return toRecord(saved);
    }

    public List<Review> getReviews(String productId) {
        return repository.findByProductId(productId).stream().map(this::toRecord).toList();
    }

    public double averageRating(String productId) {
        List<ReviewEntity> reviews = repository.findByProductId(productId);
        return reviews.stream().mapToInt(ReviewEntity::getRating).average().orElse(0.0);
    }

    private Review toRecord(ReviewEntity e) {
        return new Review(e.getId(), e.getProductId(), e.getCustomerId(), e.getRating(), e.getComment());
    }
}
