package com.example.order.dto.response;

import java.util.UUID;

public class MidtransChargeResponse {

    private String snapToken;
    private String redirectUrl;
    private UUID orderId;

    public MidtransChargeResponse(String snapToken, String redirectUrl, UUID orderId) {
        this.snapToken = snapToken;
        this.redirectUrl = redirectUrl;
        this.orderId = orderId;
    }

    public String getSnapToken() {
        return snapToken;
    }

    public void setSnapToken(String snapToken) {
        this.snapToken = snapToken;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }
}