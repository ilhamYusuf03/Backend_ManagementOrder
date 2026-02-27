package com.example.order.controller;

import com.example.order.dto.request.PaymentUpdateRequest;
import com.example.order.dto.response.MidtransChargeResponse;
import com.example.order.service.MidtransService;
import com.example.order.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final MidtransService midtransService;

    public PaymentController(
            PaymentService paymentService,
            MidtransService midtransService
    ) {
        this.paymentService = paymentService;
        this.midtransService = midtransService;
    }

    // ================= INTERNAL CALLBACK (existing) =================
    // Async - handled service - status update
    @PostMapping("/callback")
    @PreAuthorize("hasRole('PAYMENT')")
    public ResponseEntity<Void> paymentCallback(
            @Valid @RequestBody PaymentUpdateRequest request
    ) {
        paymentService.processPaymentAsync(request);
        return ResponseEntity.accepted().build();
    }

    // ================= MIDTRANS: CREATE TRANSACTION =================
    // USER request charge → dapat snap token & redirect URL
    @PostMapping("/midtrans/charge/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<MidtransChargeResponse> charge(
            @PathVariable UUID orderId
    ) {
        MidtransChargeResponse response = midtransService.createTransaction(orderId);
        return ResponseEntity.ok(response);
    }

    // ================= MIDTRANS: NOTIFICATION / WEBHOOK =================
    // Dipanggil oleh Midtrans server (public, tidak perlu auth)
    @PostMapping("/midtrans/notification")
    public ResponseEntity<Void> midtransNotification(
            @RequestBody Map<String, Object> payload
    ) {
        midtransService.handleNotification(payload);
        return ResponseEntity.ok().build();
    }

    // ================= MIDTRANS: CHECK STATUS =================
    @GetMapping("/midtrans/status/{orderId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> checkStatus(
            @PathVariable UUID orderId
    ) {
        return ResponseEntity.ok(midtransService.checkTransactionStatus(orderId));
    }
}