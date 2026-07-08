/*
package com.example.shoppingmcp.amazon;

import com.example.shoppingmcp.model.AmazonProduct;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

*/
/**
 * Client for Amazon's Creators API (the OAuth2-based replacement for the
 * retired PA-API v5, as of May 2026). Handles token acquisition/caching and
 * the searchItems operation.
 *
 * NOTE: This is a fast-moving, recently-launched API. Field names, the exact
 * token endpoint, and response shapes should be double-checked against your
 * Associates Central credential page and the current Creators API docs
 * before relying on this in production.
 *//*

// Disabled until Amazon Creators API credentials are available (needs an
// approved Associates account with 10+ qualifying sales in the last 30 days).
// Uncomment @Component below, and re-enable the corresponding wiring in
// ShoppingMcpService, once you have real AMAZON_CREDENTIAL_ID /
// AMAZON_CREDENTIAL_SECRET / AMAZON_PARTNER_TAG values.
// @Component
public class AmazonCreatorsApiClient {

    private static final String TOKEN_URL = "https://api.amazon.com/auth/o2/token";
    private static final String CATALOG_HOST = "https://creatorsapi.amazon";
    private static final String SEARCH_PATH = "/catalog/v1/searchItems";

    private final String credentialId;
    private final String credentialSecret;
    private final String partnerTag;
    private final String marketplace;

    private final RestClient restClient = RestClient.create();
    private final ReentrantLock tokenLock = new ReentrantLock();

    private volatile String cachedToken;
    private volatile Instant tokenExpiresAt = Instant.EPOCH;

    public AmazonCreatorsApiClient(
            @Value("${amazon.creators-api.credential-id:}") String credentialId,
            @Value("${amazon.creators-api.credential-secret:}") String credentialSecret,
            @Value("${amazon.creators-api.partner-tag:}") String partnerTag,
            @Value("${amazon.creators-api.marketplace:www.amazon.com}") String marketplace) {
        this.credentialId = credentialId;
        this.credentialSecret = credentialSecret;
        this.partnerTag = partnerTag;
        this.marketplace = marketplace;
    }

    */
/**
     * Searches Amazon for products matching the given keywords and returns
     * simplified results with affiliate links already tagged.
     *//*

    public List<AmazonProduct> searchItems(String keywords) {
        if (credentialId == null || credentialId.isBlank()) {
            throw new IllegalStateException(
                    "Amazon Creators API credentials are not configured. " +
                    "Set AMAZON_CREDENTIAL_ID, AMAZON_CREDENTIAL_SECRET, and AMAZON_PARTNER_TAG.");
        }

        String token = getAccessToken();

        Map<String, Object> requestBody = Map.of(
                "partnerTag", partnerTag,
                "keywords", keywords,
                "resources", List.of(
                        "images.primary.medium",
                        "itemInfo.title",
                        "offersV2.listings.price"
                )
        );

        JsonNode response = restClient.post()
                .uri(CATALOG_HOST + SEARCH_PATH)
                .header("Authorization", "Bearer " + token)
                .header("x-marketplace", marketplace)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(JsonNode.class);

        return parseSearchResponse(response);
    }

    private List<AmazonProduct> parseSearchResponse(JsonNode response) {
        List<AmazonProduct> products = new ArrayList<>();
        if (response == null) {
            return products;
        }

        JsonNode items = response.path("searchResult").path("items");
        for (JsonNode item : items) {
            String asin = item.path("asin").asText(null);
            String title = item.path("itemInfo").path("title").path("displayValue").asText(null);
            String detailPageUrl = item.path("detailPageUrl").asText(null);
            String imageUrl = item.path("images").path("primary").path("medium").path("url").asText(null);

            JsonNode listings = item.path("offersV2").path("listings");
            JsonNode priceNode = (listings.isArray() && listings.size() > 0)
                    ? listings.get(0).path("price")
                    : null;
            String price = priceNode != null ? priceNode.path("amount").asText(null) : null;
            String currency = priceNode != null ? priceNode.path("currency").asText(null) : null;

            products.add(new AmazonProduct(asin, title, price, currency, detailPageUrl, imageUrl));
        }
        return products;
    }

    */
/**
     * Returns a cached OAuth2 access token, fetching a new one if expired
     * (with a 60-second safety buffer). Thread-safe.
     *//*

    private String getAccessToken() {
        if (cachedToken != null && Instant.now().isBefore(tokenExpiresAt)) {
            return cachedToken;
        }

        tokenLock.lock();
        try {
            if (cachedToken != null && Instant.now().isBefore(tokenExpiresAt)) {
                return cachedToken;
            }

            Map<String, Object> tokenRequest = Map.of(
                    "grant_type", "client_credentials",
                    "client_id", credentialId,
                    "client_secret", credentialSecret,
                    "scope", "creatorsapi::default"
            );

            JsonNode tokenResponse = restClient.post()
                    .uri(TOKEN_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(tokenRequest)
                    .retrieve()
                    .body(JsonNode.class);

            if (tokenResponse == null || !tokenResponse.has("access_token")) {
                throw new IllegalStateException("Amazon token endpoint returned no access_token");
            }

            cachedToken = tokenResponse.path("access_token").asText();
            long expiresInSeconds = tokenResponse.path("expires_in").asLong(3600);
            tokenExpiresAt = Instant.now().plusSeconds(expiresInSeconds - 60);

            return cachedToken;
        } finally {
            tokenLock.unlock();
        }
    }
}
*/
