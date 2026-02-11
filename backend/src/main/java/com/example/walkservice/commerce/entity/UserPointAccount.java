package com.example.walkservice.commerce.entity;

import com.example.walkservice.common.exception.ApiException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class UserPointAccount {

    @Id
    private Long id;

    @Column(name = "point_balance", nullable = false)
    private Long pointBalance;

    protected UserPointAccount() {
    }

    public void spendPoints(long amount) {
        if (amount <= 0) {
            throw new ApiException("ORDER_CREATE_INVALID_AMOUNT", "Amount must be positive");
        }
        if (this.pointBalance < amount) {
            throw new ApiException("ORDER_CREATE_POINT_INSUFFICIENT", "Insufficient point balance");
        }
        this.pointBalance = this.pointBalance - amount;
    }

    public void refundPoints(long amount) {
        if (amount <= 0) {
            throw new ApiException("ORDER_CANCEL_INVALID_AMOUNT", "Amount must be positive");
        }
        this.pointBalance = this.pointBalance + amount;
    }

    public Long getId() {
        return id;
    }

    public Long getPointBalance() {
        return pointBalance;
    }
}
