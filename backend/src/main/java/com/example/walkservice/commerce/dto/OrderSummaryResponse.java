package com.example.walkservice.commerce.dto;

import com.example.walkservice.commerce.entity.OrderStatus;
import java.time.OffsetDateTime;

public class OrderSummaryResponse {

    private final Long id;
    private final OrderStatus status;
    private final Long totalPrice;
    private final OffsetDateTime createdAt;

    public OrderSummaryResponse(Long id, OrderStatus status, Long totalPrice, OffsetDateTime createdAt) {
        this.id = id;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
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
}
