package com.example.walkservice.commerce.service;

import com.example.walkservice.commerce.dto.PageMetaResponse;
import com.example.walkservice.commerce.dto.ProductPageDataResponse;
import com.example.walkservice.commerce.dto.ProductResponse;
import com.example.walkservice.commerce.dto.ProductSummaryResponse;
import com.example.walkservice.commerce.entity.Product;
import com.example.walkservice.commerce.entity.ProductCategory;
import com.example.walkservice.commerce.entity.ProductStatus;
import com.example.walkservice.commerce.repository.ProductRepository;
import com.example.walkservice.common.exception.ApiException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductPageDataResponse listProducts(Pageable pageable, ProductCategory category, ProductStatus status) {
        String categoryFilter = category == null ? null : category.name();
        String statusFilter = status == null ? null : status.name();

        Page<Product> productPage = productRepository.findAllWithFilters(categoryFilter, statusFilter, pageable);
        List<ProductSummaryResponse> content = productPage.getContent().stream().map(this::toSummaryResponse).toList();

        return new ProductPageDataResponse(
                content,
                new PageMetaResponse(
                        productPage.getNumber(),
                        productPage.getSize(),
                        productPage.getTotalElements(),
                        productPage.getTotalPages()
                )
        );
    }

    public ProductResponse getProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ApiException("PRODUCT_FIND_NOT_FOUND", "Product not found"));
        return toResponse(product);
    }

    private ProductSummaryResponse toSummaryResponse(Product product) {
        return new ProductSummaryResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus()
        );
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getCategory(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getStatus(),
                product.getDescription(),
                product.getCreatedAt()
        );
    }
}
