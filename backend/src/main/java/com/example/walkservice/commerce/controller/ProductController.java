package com.example.walkservice.commerce.controller;

import com.example.walkservice.commerce.dto.ProductPageDataResponse;
import com.example.walkservice.commerce.dto.ProductResponse;
import com.example.walkservice.commerce.entity.ProductCategory;
import com.example.walkservice.commerce.entity.ProductStatus;
import com.example.walkservice.commerce.service.ProductService;
import com.example.walkservice.common.response.ApiResponse;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<ProductPageDataResponse> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) ProductStatus status
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(productService.listProducts(pageable, category, status));
    }

    @GetMapping("/{productId}")
    public ApiResponse<ProductResponse> get(@PathVariable Long productId) {
        return ApiResponse.success(productService.getProduct(productId));
    }
}
