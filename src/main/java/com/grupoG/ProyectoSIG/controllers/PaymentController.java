package com.grupoG.ProyectoSIG.controllers;

import com.grupoG.ProyectoSIG.dto.PaymentRequest;
import com.grupoG.ProyectoSIG.services.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody PaymentRequest request) {
        try {
            PaymentIntent paymentIntent = paymentService.createPaymentIntent(
                    request.getAmount(),
                    request.getCurrency()
            );

            Map<String, String> response = new HashMap<>();
            response.put("client_secret", paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(@RequestBody Map<String, String> request) {
        try {
            String paymentIntentId = request.get("payment_intent_id");
            PaymentIntent paymentIntent = paymentService.confirmPayment(paymentIntentId);

            return ResponseEntity.ok(paymentIntent);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/intent/{id}")
    public ResponseEntity<?> getPaymentIntent(@PathVariable String id) {
        try {
            PaymentIntent paymentIntent = paymentService.getPaymentIntent(id);
            return ResponseEntity.ok(paymentIntent);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Validation Error: " + e.getMessage());
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Stripe Error: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}
