package com.example.walkservice.commerce.dto;

public class OrderItemResponse {

    private final Long id;
    private final Long productId;
    private final Integer quantity;
    private final Long unitPrice;
    private final Long subtotalPrice;

    public OrderItemResponse(Long id, Long productId, Integer quantity, Long unitPrice, Long subtotalPrice) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.subtotalPrice = subtotalPrice;
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Long getUnitPrice() {
        return unitPrice;
    }

    public Long getSubtotalPrice() {
        return subtotalPrice;
    }
}
