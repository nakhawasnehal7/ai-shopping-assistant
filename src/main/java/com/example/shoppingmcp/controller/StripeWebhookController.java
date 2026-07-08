package com.example.shoppingmcp.controller;

import com.example.shoppingmcp.service.OrderService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Receives Stripe webhook events so order status stays correct even when
 * payment confirmation happens asynchronously (e.g. a card requiring extra
 * verification steps that complete after the initial API call returns).
 *
 * For local testing, use the Stripe CLI:
 *   stripe listen --forward-to localhost:8080/webhooks/stripe
 * It prints a webhook signing secret to put in STRIPE_WEBHOOK_SECRET.
 */
@RestController
@RequestMapping("/webhooks")
public class StripeWebhookController {

    private final String webhookSecret;
    private final OrderService orderService;

    public StripeWebhookController(
            @Value("${stripe.webhook-secret}") String webhookSecret,
            OrderService orderService) {
        this.webhookSecret = webhookSecret;
        this.orderService = orderService;
    }

    @PostMapping("/stripe")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String signatureHeader) {

        if (webhookSecret == null || webhookSecret.isBlank()) {
            // Not configured — accept but no-op, so Stripe doesn't retry forever
            return ResponseEntity.ok("webhook secret not configured, ignoring");
        }

        Event event;
        try {
            event = Webhook.constructEvent(payload, signatureHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().body("Invalid signature");
        }

        StripeObject dataObject = event.getDataObjectDeserializer().getObject().orElse(null);

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                if (dataObject instanceof PaymentIntent intent) {
                    orderService.markPaymentSucceeded(intent.getId());
                }
            }
            case "payment_intent.payment_failed" -> {
                if (dataObject instanceof PaymentIntent intent) {
                    orderService.markPaymentFailed(intent.getId());
                }
            }
            default -> {
                // Ignore event types we don't handle
            }
        }

        return ResponseEntity.ok("received");
    }
}
