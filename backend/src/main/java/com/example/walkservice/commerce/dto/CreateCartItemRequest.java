package com.example.walkservice.commerce.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateCartItemRequest {

    @NotNull
    private final Long productId;

    @NotNull
    @Min(1)
    @Max(99)
    private final Integer quantity;

    public CreateCartItemRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
