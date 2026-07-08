package com.example.shoppingmcp.resource;

import com.example.shoppingmcp.model.Product;
import com.example.shoppingmcp.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.spec.McpSchema.ReadResourceResult;
import io.modelcontextprotocol.spec.McpSchema.TextResourceContents;
import org.springaicommunity.mcp.annotation.McpResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Exposes the product catalog as browsable MCP resources — clients can list
 * and read these directly, without needing to call a search tool with a query.
 * Useful for clients that want to show "browse the catalog" UI, or for an
 * LLM that wants full context on what's available before deciding what to search for.
 */
@Component
public class ShoppingMcpResources {

    private final ProductRepository products;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ShoppingMcpResources(ProductRepository products) {
        this.products = products;
    }

    @McpResource(
            uri = "shopping://products",
            name = "All Products",
            description = "The full product catalog, browsable without a search query")
    public ReadResourceResult listAllProducts() {
        List<Product> all = products.findAll();
        String json = toJson(all);
        return new ReadResourceResult(List.of(
                new TextResourceContents("shopping://products", "application/json", json)
        ));
    }

    @McpResource(
            uri = "shopping://products/{productId}",
            name = "Product Detail",
            description = "A single product's full detail, addressed directly by ID")
    public ReadResourceResult getProduct(String productId) {
        return products.findById(productId)
                .map(product -> new ReadResourceResult(List.of(
                        new TextResourceContents("shopping://products/" + productId,
                                "application/json", toJson(product))
                )))
                .orElseGet(() -> new ReadResourceResult(List.of(
                        new TextResourceContents("shopping://products/" + productId,
                                "text/plain", "Product not found: " + productId)
                )));
    }

    private String toJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize resource content", e);
        }
    }
}
