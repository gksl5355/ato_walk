package com.example.walkservice.commerce.entity;

import com.example.walkservice.common.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_price", nullable = false)
    private Long totalPrice;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Order() {
    }

    public Order(Long userId, OrderStatus status, Long totalPrice, OffsetDateTime createdAt) {
        this.userId = userId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.createdAt = createdAt;
    }

    public void cancel() {
        if (this.status != OrderStatus.CREATED) {
            throw new ApiException("ORDER_CANCEL_INVALID_STATE", "Order can only be canceled when created");
        }
        this.status = OrderStatus.CANCELED;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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
