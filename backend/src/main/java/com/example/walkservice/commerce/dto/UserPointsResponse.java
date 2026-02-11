package com.example.walkservice.commerce.dto;

public class UserPointsResponse {

    private final Long balance;

    public UserPointsResponse(Long balance) {
        this.balance = balance;
    }

    public Long getBalance() {
        return balance;
    }
}
