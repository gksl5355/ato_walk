package com.example.walkservice.commerce.dto;

import com.example.walkservice.commerce.entity.PointTransactionType;
import java.time.OffsetDateTime;

public class PointTransactionResponse {

    private final Long id;
    private final Long orderId;
    private final PointTransactionType type;
    private final Long amount;
    private final Long balanceAfter;
    private final OffsetDateTime createdAt;

    public PointTransactionResponse(
            Long id,
            Long orderId,
            PointTransactionType type,
            Long amount,
            Long balanceAfter,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.orderId = orderId;
        this.type = type;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public PointTransactionType getType() {
        return type;
    }

    public Long getAmount() {
        return amount;
    }

    public Long getBalanceAfter() {
        return balanceAfter;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
