package com.example.order.service;

import com.example.order.dto.response.MidtransChargeResponse;

import java.util.Map;
import java.util.UUID;

public interface MidtransService {

    // Charge / Create Transaction → return Snap token & redirect URL
    MidtransChargeResponse createTransaction(UUID orderId);

    // Notification / Webhook → proses notifikasi dari Midtrans
    void handleNotification(Map<String, Object> payload);

    // Check Transaction Status by orderId
    Map<String, Object> checkTransactionStatus(UUID orderId);
}