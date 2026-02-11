package com.example.walkservice.commerce.dto;

import java.util.List;

public class ProductPageDataResponse {

    private final List<ProductSummaryResponse> content;
    private final PageMetaResponse page;

    public ProductPageDataResponse(List<ProductSummaryResponse> content, PageMetaResponse page) {
        this.content = content;
        this.page = page;
    }

    public List<ProductSummaryResponse> getContent() {
        return content;
    }

    public PageMetaResponse getPage() {
        return page;
    }
}
