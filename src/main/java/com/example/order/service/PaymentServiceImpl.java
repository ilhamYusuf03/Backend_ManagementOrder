package com.example.order.service;

import com.example.order.constant.OrderStatus;
import com.example.order.dto.request.PaymentUpdateRequest;
import com.example.order.entity.Order;
import com.example.order.exception.BusinessException;
import com.example.order.exception.ErrorCode;
import com.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;


import java.time.Instant;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;

    public PaymentServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Async("paymentExecutor")
@Transactional
@Override
public void processPaymentAsync(PaymentUpdateRequest request) {

    Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new BusinessException(
                    ErrorCode.ORDER_NOT_FOUND,
                    "Order not found"
            ));

    if (order.getPaymentReference() != null) return;
    if (order.getStatus() != OrderStatus.PENDING) return;

    order.setPaymentReference(request.getPaymentReference());

    if (request.getStatus() == PaymentUpdateRequest.PaymentStatus.PAID) {
        order.setStatus(OrderStatus.PAID);
        order.setPaidAt(Instant.now());
    } else {
        order.setStatus(OrderStatus.FAILED);
    }

    orderRepository.save(order);
}
}
