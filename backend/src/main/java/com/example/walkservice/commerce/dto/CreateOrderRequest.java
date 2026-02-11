package com.example.walkservice.commerce.dto;

import com.example.walkservice.commerce.entity.PaymentMethod;

public class CreateOrderRequest {

    private PaymentMethod paymentMethod;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
