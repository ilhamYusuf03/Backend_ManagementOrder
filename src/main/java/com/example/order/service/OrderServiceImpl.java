package com.example.order.service;

import com.example.order.constant.OrderStatus;
import com.example.order.dto.request.CreateOrderRequest;
import com.example.order.dto.response.OrderResponse;
import com.example.order.entity.Order;
import com.example.order.entity.OrderItem;
import com.example.order.exception.BusinessException;
import com.example.order.exception.ErrorCode;
import com.example.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Override
    public OrderResponse createOrder(CreateOrderRequest request) {

        Order order = new Order();
        order.setCustomerId(request.getCustomerId());

        List<OrderItem> items = request.getItems()
                .stream()
                .map(reqItem -> {
                    OrderItem item = new OrderItem();
                    item.setOrder(order);
                    item.setProductId(reqItem.getProductId());
                    item.setQuantity(reqItem.getQuantity());
                    item.setPrice(reqItem.getPrice());
                    return item;
                })
                .collect(Collectors.toList());

        long totalAmount = items.stream()
                .mapToLong(i -> i.getPrice() * i.getQuantity())
                .sum();

        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setItems(items);

        Order savedOrder = orderRepository.save(order);

        return mapToResponse(savedOrder);
    }

    @Override
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ORDER_NOT_FOUND,
                        "Order not found"
                ));

        return mapToResponse(order);
    }

    @Override
    public Page<OrderResponse> getOrders(
            String customerId,
            OrderStatus status,
            Pageable pageable
    ) {
        Page<Order> orders;

        if (customerId != null && status != null) {
            orders = orderRepository.findByCustomerIdAndStatus(customerId, status, pageable);
        } else if (customerId != null) {
            orders = orderRepository.findByCustomerId(customerId, pageable);
        } else if (status != null) {
            orders = orderRepository.findByStatus(status, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(this::mapToResponse);
    }

    // Admin: reuse getOrders karena logic sudah mendukung semua skenario
    @Override
    public Page<OrderResponse> getAllOrdersForAdmin(
            String customerId,
            OrderStatus status,
            Pageable pageable
    ) {
        return getOrders(customerId, status, pageable);
    }

    // ================== MAPPER ==================
    private OrderResponse mapToResponse(Order order) {

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getId());
        response.setCustomerId(order.getCustomerId());
        response.setStatus(order.getStatus());
        response.setTotalAmount(order.getTotalAmount());
        response.setCreatedAt(order.getCreatedAt());

        List<OrderResponse.OrderItemResponse> items =
                order.getItems().stream().map(item -> {
                    OrderResponse.OrderItemResponse r =
                            new OrderResponse.OrderItemResponse();
                    r.setProductId(item.getProductId());
                    r.setQuantity(item.getQuantity());
                    r.setPrice(item.getPrice());
                    return r;
                }).collect(Collectors.toList());

        response.setItems(items);
        return response;
    }
}