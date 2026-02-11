package com.example.walkservice.commerce.dto;

import com.example.walkservice.commerce.entity.ProductCategory;
import com.example.walkservice.commerce.entity.ProductStatus;
import java.time.OffsetDateTime;

public class ProductResponse {

    private final Long id;
    private final String name;
    private final ProductCategory category;
    private final Long price;
    private final Integer stockQuantity;
    private final ProductStatus status;
    private final String description;
    private final OffsetDateTime createdAt;

    public ProductResponse(
            Long id,
            String name,
            ProductCategory category,
            Long price,
            Integer stockQuantity,
            ProductStatus status,
            String description,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public Long getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
