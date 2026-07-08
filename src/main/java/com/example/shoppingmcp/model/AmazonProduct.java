package com.example.shoppingmcp.model;

/**
 * A simplified view of an Amazon product returned by the Creators API,
 * with the affiliate DetailPageURL already tagged with your partner tag.
 */
public record AmazonProduct(
        String asin,
        String title,
        String price,
        String currency,
        String detailPageUrl,
        String imageUrl
) {}
