package com.example.order.dto.response;

import com.example.order.constant.OrderStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class OrderResponse {

    private UUID orderId;
    private String customerId;
    private OrderStatus status;
    private Long totalAmount;
    private Instant createdAt;
    private List<OrderItemResponse> items;

    public UUID getOrderId() {
        return orderId;
    }

    public void setOrderId(UUID orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Long getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Long totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public void setItems(List<OrderItemResponse> items) {
        this.items = items;
    }

    // ===== INNER RESPONSE =====
    public static class OrderItemResponse {

        private String productId;
        private Integer quantity;
        private Long price;

        public String getProductId() {
            return productId;
        }

        public void setProductId(String productId) {
            this.productId = productId;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public Long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }
    }
}
