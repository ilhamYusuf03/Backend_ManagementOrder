package com.example.order.service;

import com.example.order.dto.request.PaymentUpdateRequest;

public interface PaymentService {
    void processPaymentAsync(PaymentUpdateRequest request);
}
