package com.example.walkservice.commerce.dto;

import com.example.walkservice.commerce.entity.OrderStatus;
import java.time.OffsetDateTime;
import java.util.List;

public class OrderResponse {

    private final Long id;
    private final OrderStatus status;
    private final Long totalPrice;
    private final OffsetDateTime createdAt;
    private final List<OrderItemResponse> items;

    public OrderResponse(Long id, OrderStatus status, Long totalPrice, OffsetDateTime createdAt, List<OrderItemResponse> items) {
        this.id = id;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Long getTotalPrice() {
        return totalPrice;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }
}
