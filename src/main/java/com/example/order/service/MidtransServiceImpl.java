package com.example.order.service;

import com.example.order.constant.OrderStatus;
import com.example.order.dto.response.MidtransChargeResponse;
import com.example.order.entity.Order;
import com.example.order.exception.BusinessException;
import com.example.order.exception.ErrorCode;
import com.example.order.repository.OrderRepository;
import com.midtrans.httpclient.error.MidtransError;
import com.midtrans.service.MidtransCoreApi;
import com.midtrans.service.MidtransSnapApi;
import jakarta.transaction.Transactional;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class MidtransServiceImpl implements MidtransService {

    private final MidtransSnapApi snapApi;
    private final MidtransCoreApi coreApi;
    private final OrderRepository orderRepository;

    public MidtransServiceImpl(
            MidtransSnapApi snapApi,
            MidtransCoreApi coreApi,
            OrderRepository orderRepository
    ) {
        this.snapApi = snapApi;
        this.coreApi = coreApi;
        this.orderRepository = orderRepository;
    }

    // ================= CHARGE / CREATE TRANSACTION =================
    @Override
    @Transactional
    public MidtransChargeResponse createTransaction(UUID orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ORDER_NOT_FOUND,
                        "Order not found"
                ));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BusinessException(
                    ErrorCode.INVALID_ORDER_STATUS,
                    "Order is not in PENDING status"
            );
        }

        // Build Midtrans request payload
        Map<String, Object> params = new HashMap<>();

        // Transaction details
        Map<String, Object> transactionDetails = new HashMap<>();
        transactionDetails.put("order_id", order.getId().toString());
        transactionDetails.put("gross_amount", order.getTotalAmount());
        params.put("transaction_details", transactionDetails);

        // Customer details
        Map<String, Object> customerDetails = new HashMap<>();
        customerDetails.put("customer_id", order.getCustomerId());
        params.put("customer_details", customerDetails);

        // Enabled payment methods: QRIS & Virtual Account
        params.put("enabled_payments", List.of(
                "gopay",          // QRIS/GoPay
                "qris",           // QRIS universal
                "bca_va",         // BCA Virtual Account
                "bni_va",         // BNI Virtual Account
                "bri_va",         // BRI Virtual Account
                "mandiri_va"      // Mandiri Virtual Account
        ));

        try {
            JSONObject result = snapApi.createTransaction(params);

            String snapToken = result.getString("token");
            String redirectUrl = result.getString("redirect_url");

            // Simpan snap token ke order
            order.setSnapToken(snapToken);
            orderRepository.save(order);

            return new MidtransChargeResponse(snapToken, redirectUrl, orderId);

        } catch (MidtransError e) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_GATEWAY_ERROR,
                    "Failed to create Midtrans transaction: " + e.getMessage()
            );
        }
    }

    // ================= NOTIFICATION / WEBHOOK =================
    @Override
    @Transactional
    public void handleNotification(Map<String, Object> payload) {

        try {
            JSONObject notification = new JSONObject(payload);

            // Verify notification dari Midtrans
            JSONObject verified = coreApi.checkTransaction(
                    notification.getString("order_id")
            );

            String orderId        = verified.getString("order_id");
            String transactionStatus = verified.getString("transaction_status");
            String fraudStatus    = verified.optString("fraud_status", "accept");
            String paymentType    = verified.getString("payment_type");
            String transactionId  = verified.getString("transaction_id");

            Order order = orderRepository.findById(UUID.fromString(orderId))
                    .orElseThrow(() -> new BusinessException(
                            ErrorCode.ORDER_NOT_FOUND,
                            "Order not found"
                    ));

            // Idempotent: skip jika sudah diproses
            if (order.getStatus() != OrderStatus.PENDING) return;

            order.setPaymentReference(transactionId);
            order.setPaymentType(paymentType);

            // Mapping status Midtrans → OrderStatus
            if ("capture".equals(transactionStatus) && "accept".equals(fraudStatus)) {
                order.setStatus(OrderStatus.PAID);
                order.setPaidAt(Instant.now());
            } else if ("settlement".equals(transactionStatus)) {
                order.setStatus(OrderStatus.PAID);
                order.setPaidAt(Instant.now());
            } else if ("deny".equals(transactionStatus)
                    || "cancel".equals(transactionStatus)
                    || "expire".equals(transactionStatus)
                    || "failure".equals(transactionStatus)) {
                order.setStatus(OrderStatus.FAILED);
            }

            orderRepository.save(order);

        } catch (MidtransError e) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_GATEWAY_ERROR,
                    "Failed to verify Midtrans notification: " + e.getMessage()
            );
        }
    }

    // ================= CHECK TRANSACTION STATUS =================
    @Override
    public Map<String, Object> checkTransactionStatus(UUID orderId) {

        try {
            JSONObject result = coreApi.checkTransaction(orderId.toString());

            Map<String, Object> response = new HashMap<>();
            response.put("order_id", result.getString("order_id"));
            response.put("transaction_status", result.getString("transaction_status"));
            response.put("payment_type", result.optString("payment_type", "-"));
            response.put("gross_amount", result.optString("gross_amount", "0"));
            response.put("transaction_id", result.optString("transaction_id", "-"));
            response.put("transaction_time", result.optString("transaction_time", "-"));

            return response;

        } catch (MidtransError e) {
            throw new BusinessException(
                    ErrorCode.PAYMENT_GATEWAY_ERROR,
                    "Failed to check transaction status: " + e.getMessage()
            );
        }
    }
}