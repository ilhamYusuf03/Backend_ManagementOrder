package com.example.order.service;

import com.example.order.constant.OrderStatus;
import com.example.order.dto.request.CreateOrderRequest;
import com.example.order.dto.response.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(CreateOrderRequest request);

    OrderResponse getOrderById(UUID orderId);

    Page<OrderResponse> getOrders(
            String customerId,
            OrderStatus status,
            Pageable pageable
    );

    // Admin: akses semua order tanpa batasan
    Page<OrderResponse> getAllOrdersForAdmin(
            String customerId,
            OrderStatus status,
            Pageable pageable
    );
}