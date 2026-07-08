package com.example.shoppingmcp.service;

// import com.example.shoppingmcp.amazon.AmazonCreatorsApiClient;  // disabled: no Amazon credentials yet
// import com.example.shoppingmcp.model.AmazonProduct;             // disabled: no Amazon credentials yet

import com.example.shoppingmcp.model.*;
import com.example.shoppingmcp.repository.ProductRepository;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Exposes the shopping backend as MCP tools. Spring AI auto-discovers every
 *
 * @McpTool method on this bean and registers it with the MCP server,
 * generating the JSON schema for each parameter from the method signature
 * and @McpToolParam descriptions.
 */
@Service
public class ShoppingMcpService {

    private final ProductRepository products;
    private final OrderService orders;
    // private final AmazonCreatorsApiClient amazonClient;  // disabled: no Amazon credentials yet
    private final CartService cartService;
    private final ReviewService reviewService;
    private final AnalyticsService analyticsService;
    private final CouponService couponService;
/*
    private final AmazonCreatorsApiClient amazonClient;
*/

    public ShoppingMcpService(ProductRepository products, OrderService orders,
            /* AmazonCreatorsApiClient amazonClient, */ CartService cartService,
                              ReviewService reviewService, AnalyticsService analyticsService,
                              CouponService couponService) {
        this.products = products;
        this.orders = orders;
        // this.amazonClient = amazonClient;  // disabled: no Amazon credentials yet
        this.cartService = cartService;
        this.reviewService = reviewService;
        this.analyticsService = analyticsService;
        this.couponService = couponService;
/*
        this.amazonClient = amazonClient;
*/
    }

    // ---------- Catalog ----------

    @McpTool(name = "search_products", description = "Search the product catalog by keyword and optional category")
    public List<Product> searchProducts(
            @McpToolParam(description = "Search keyword, e.g. 'wireless headphones'", required = true) String query,
            @McpToolParam(description = "Optional category filter, e.g. 'audio' or 'accessories'", required = false) String category) {
        return products.search(query, category);
    }

    @McpTool(name = "get_product_details", description = "Get full details for a single product by ID")
    public Product getProductDetails(
            @McpToolParam(description = "Product ID, e.g. 'P-1001'", required = true) String productId) {
        return products.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
    }

    @McpTool(name = "check_stock", description = "Check current stock quantity for a product")
    public int checkStock(
            @McpToolParam(description = "Product ID", required = true) String productId) {
/*
        return products.findById(productId).map(Product::stockQty).orElse(0);
*/

        return 0;

    }

    @McpTool(name = "add_product", description = "Add a new product to the catalog and return the generated product with its assigned ID")
    public Product addProduct(
            @McpToolParam(description = "Product name", required = true) String name,
            @McpToolParam(description = "Product description", required = true) String description,
            @McpToolParam(description = "Price in USD, e.g. 49.99", required = true) BigDecimal price,
            @McpToolParam(description = "Initial stock quantity", required = true) int stockQty,
            @McpToolParam(description = "Category, e.g. 'audio', 'accessories', 'displays'", required = true) String category) {
        return products.addProduct(name, description, price, stockQty, category);
    }

    // ---------- Orders & payment ----------

    @McpTool(name = "place_order", description = "Place an order for a product and quantity, applying an optional coupon and charging payment immediately")
    public Order placeOrder(
            @McpToolParam(description = "Product ID", required = true) String productId,
            @McpToolParam(description = "Quantity to order", required = true) int quantity,
            @McpToolParam(description = "Optional customer identifier, for order history lookups", required = false) String customerId,
            @McpToolParam(description = "Optional coupon code, e.g. 'WELCOME10'", required = false) String couponCode) {
        return orders.create(productId, quantity, customerId, couponCode);
    }

    @McpTool(name = "check_order_status", description = "Check the status of an existing order")
    public Order checkOrderStatus(
            @McpToolParam(description = "Order ID, e.g. 'ORD-1'", required = true) String orderId) {
        return orders.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + orderId));
    }

    @McpTool(name = "cancel_order", description = "Cancel a paid order and refund the payment in full; restores stock")
    public Order cancelOrder(
            @McpToolParam(description = "Order ID to cancel, e.g. 'ORD-1'", required = true) String orderId) {
        return orders.cancelOrder(orderId);
    }

    @McpTool(name = "list_orders", description = "List order history, optionally filtered to a specific customer")
    public List<Order> listOrders(
            @McpToolParam(description = "Optional customer ID to filter by; omit to list all orders", required = false) String customerId) {
        return orders.listOrders(customerId);
    }

    // ---------- Coupons ----------

    @McpTool(name = "validate_coupon", description = "Check whether a coupon code is valid and return its discount percentage")
    public String validateCoupon(
            @McpToolParam(description = "Coupon code, e.g. 'WELCOME10'", required = true) String code) {
        return couponService.validate(code)
                .map(pct -> pct + "% off")
                .orElse("Invalid or inactive coupon code");
    }

    // ---------- Cart ----------

    @McpTool(name = "add_to_cart", description = "Add a product and quantity to a customer's cart")
    public Map<String, Integer> addToCart(
            @McpToolParam(description = "Customer ID", required = true) String customerId,
            @McpToolParam(description = "Product ID", required = true) String productId,
            @McpToolParam(description = "Quantity to add", required = true) int quantity) {
        return cartService.addItem(customerId, productId, quantity);
    }

    @McpTool(name = "remove_from_cart", description = "Remove a product entirely from a customer's cart")
    public Map<String, Integer> removeFromCart(
            @McpToolParam(description = "Customer ID", required = true) String customerId,
            @McpToolParam(description = "Product ID to remove", required = true) String productId) {
        return cartService.removeItem(customerId, productId);
    }

    @McpTool(name = "view_cart", description = "View the current contents of a customer's cart")
    public Map<String, Integer> viewCart(
            @McpToolParam(description = "Customer ID", required = true) String customerId) {
        return cartService.viewCart(customerId);
    }

    @McpTool(name = "checkout_cart", description = "Check out all items in a customer's cart, charging payment for each and clearing the cart")
    public List<Order> checkoutCart(
            @McpToolParam(description = "Customer ID", required = true) String customerId,
            @McpToolParam(description = "Optional coupon code applied to every line item", required = false) String couponCode) {
        return cartService.checkout(customerId, couponCode);
    }

    // ---------- Reviews ----------

    @McpTool(name = "add_review", description = "Add a customer review and rating (1-5) for a product")
    public Review addReview(
            @McpToolParam(description = "Product ID", required = true) String productId,
            @McpToolParam(description = "Customer ID", required = true) String customerId,
            @McpToolParam(description = "Rating from 1 to 5", required = true) int rating,
            @McpToolParam(description = "Review comment text", required = false) String comment) {
        return reviewService.addReview(productId, customerId, rating, comment);
    }

    @McpTool(name = "get_reviews", description = "Get all reviews and the average rating for a product")
    public String getReviews(
            @McpToolParam(description = "Product ID", required = true) String productId) {
        List<Review> reviews = reviewService.getReviews(productId);
        double avg = reviewService.averageRating(productId);
        return "Average rating: " + avg + " (" + reviews.size() + " reviews): " + reviews;
    }

    // ---------- Admin / analytics ----------

    @McpTool(name = "get_sales_summary", description = "Get an admin summary: total orders, revenue, top-selling products, and low-stock alerts")
    public SalesSummary getSalesSummary() {
        return analyticsService.getSalesSummary();
    }

    // ---------- Amazon (affiliate search only) ----------

    @McpTool(name = "search_amazon_products",
            description = "Search Amazon.com for products (NOT the internal catalog) and return results with affiliate links. " +
                    "Use this only when the user explicitly asks about Amazon or wants products not found in the internal catalog. " +
                    "This cannot place an order — the user must complete the purchase on Amazon themselves via the returned link.")
    public List<AmazonProduct> searchAmazonProducts(
            @McpToolParam(description = "Search keywords, e.g. 'wireless headphones'", required = true) String query) {
/*
        return amazonClient.searchItems(query);
*/

        return null;
    }
}
