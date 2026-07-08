package com.example.shoppingmcp.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Wraps Stripe PaymentIntent creation/confirmation.
 *
 * IMPORTANT — read before treating this as production-ready:
 * Real checkouts should collect card details on the CLIENT side using
 * Stripe's Payment Element / Elements.js, which tokenizes the card and
 * never lets raw card numbers touch your server (required for PCI
 * compliance). This service uses Stripe's built-in TEST payment method
 * token ("pm_card_visa") to simulate a full charge end-to-end for this
 * demo, since there's no web checkout UI collecting a real card here.
 * Swap the hardcoded test payment method for a real payment_method_id
 * passed up from a client-side Payment Element before going live.
 */
@Service
public class StripePaymentService {

    private static final String TEST_PAYMENT_METHOD = "pm_card_visa";

    private final String apiKey;

    public StripePaymentService(@Value("${stripe.api-key}") String apiKey) {
        this.apiKey = apiKey;
    }

    @PostConstruct
    void init() {
        if (apiKey != null && !apiKey.isBlank()) {
            Stripe.apiKey = apiKey;
        }
    }

    /**
     * Creates and confirms a PaymentIntent for the given amount.
     * Returns the PaymentIntent ID on success.
     * Throws IllegalStateException if Stripe isn't configured, or
     * RuntimeException if the charge fails/is declined.
     */
    public String charge(BigDecimal amountUsd, String description) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "Stripe is not configured. Set the STRIPE_API_KEY environment variable " +
                    "with a test secret key from https://dashboard.stripe.com/test/apikeys");
        }

        long amountInCents = amountUsd.multiply(BigDecimal.valueOf(100)).longValueExact();

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency("usd")
                    .setDescription(description)
                    .setPaymentMethod(TEST_PAYMENT_METHOD)
                    .setConfirm(true)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build()
                    )
                    .build();

            PaymentIntent intent = PaymentIntent.create(params);

            if (!"succeeded".equals(intent.getStatus())) {
                throw new RuntimeException("Payment did not succeed, status: " + intent.getStatus());
            }

            return intent.getId();
        } catch (StripeException e) {
            throw new RuntimeException("Payment failed: " + e.getMessage(), e);
        }
    }

    /**
     * Fully refunds a previously captured PaymentIntent.
     * Returns the Stripe Refund ID on success.
     */
    public String refund(String paymentIntentId) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("Stripe is not configured. Set STRIPE_API_KEY.");
        }

        try {
            RefundCreateParams params = RefundCreateParams.builder()
                    .setPaymentIntent(paymentIntentId)
                    .build();

            Refund refund = Refund.create(params);

            if (!"succeeded".equals(refund.getStatus()) && !"pending".equals(refund.getStatus())) {
                throw new RuntimeException("Refund did not succeed, status: " + refund.getStatus());
            }

            return refund.getId();
        } catch (StripeException e) {
            throw new RuntimeException("Refund failed: " + e.getMessage(), e);
        }
    }
}
