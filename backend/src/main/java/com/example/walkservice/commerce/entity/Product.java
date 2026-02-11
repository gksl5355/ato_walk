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
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ProductCategory category;

    @Column(name = "price", nullable = false)
    private Long price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ProductStatus status;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Product() {
    }

    public Product(
            String name,
            ProductCategory category,
            Long price,
            Integer stockQuantity,
            ProductStatus status,
            String description,
            OffsetDateTime createdAt
    ) {
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.status = status;
        this.description = description;
        this.createdAt = createdAt;
    }

    public boolean canPutInCart() {
        return this.status == ProductStatus.ON_SALE && this.stockQuantity > 0;
    }

    public boolean hasStockFor(int quantity) {
        return this.stockQuantity >= quantity;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new ApiException("ORDER_CREATE_INVALID_QUANTITY", "Quantity must be positive");
        }
        if (this.status != ProductStatus.ON_SALE) {
            throw new ApiException("ORDER_CREATE_STOCK_NOT_ENOUGH", "Product is not on sale");
        }
        if (this.stockQuantity < quantity) {
            throw new ApiException("ORDER_CREATE_STOCK_NOT_ENOUGH", "Insufficient stock");
        }

        this.stockQuantity = this.stockQuantity - quantity;
        if (this.stockQuantity == 0) {
            this.status = ProductStatus.SOLD_OUT;
        }
    }

    public void increaseStock(int quantity) {
        if (quantity <= 0) {
            throw new ApiException("ORDER_CANCEL_INVALID_QUANTITY", "Quantity must be positive");
        }

        this.stockQuantity = this.stockQuantity + quantity;
        if (this.status == ProductStatus.SOLD_OUT) {
            this.status = ProductStatus.ON_SALE;
        }
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
