package com.example.shoppingmcp.controller;

import com.example.shoppingmcp.controller.dto.AddReviewRequest;
import com.example.shoppingmcp.model.Review;
import com.example.shoppingmcp.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products/{productId}/reviews")
public class ReviewRestController {

    private final ReviewService reviewService;

    public ReviewRestController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping
    public Map<String, Object> getReviews(@PathVariable String productId) {
        List<Review> reviews = reviewService.getReviews(productId);
        double average = reviewService.averageRating(productId);
        return Map.of("averageRating", average, "count", reviews.size(), "reviews", reviews);
    }

    @PostMapping
    public ResponseEntity<Review> addReview(@PathVariable String productId, @RequestBody AddReviewRequest request) {
        Review review = reviewService.addReview(productId, request.customerId(), request.rating(), request.comment());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }
}
