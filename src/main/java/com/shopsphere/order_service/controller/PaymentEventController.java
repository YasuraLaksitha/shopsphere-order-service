package com.shopsphere.order_service.controller;

import com.shopsphere.order_service.services.IEventService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaymentEventController {

    @Value("${WEBHOOK_SECRET}")
    private String webhookSecretKey;

    private final IEventService eventService;

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {

        Event event;

        try {
            event = Webhook.constructEvent(payload,signature,webhookSecretKey);
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }
        eventService.handlePaymentEvent(event);
        return ResponseEntity.ok("success");
    }
}