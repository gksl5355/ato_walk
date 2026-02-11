package com.example.walkservice.commerce.dto;

public class CartItemResponse {

    private final Long id;
    private final Long productId;
    private final String productName;
    private final Integer quantity;
    private final Long unitPrice;
    private final Long subtotalPrice;

    public CartItemResponse(Long id, Long productId, String productName, Integer quantity, Long unitPrice, Long subtotalPrice) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
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

    public String getProductName() {
        return productName;
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
