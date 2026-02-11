package com.example.walkservice.commerce.dto;

import java.util.List;

public class OrderSummaryPageDataResponse {

    private final List<OrderSummaryResponse> content;
    private final PageMetaResponse page;

    public OrderSummaryPageDataResponse(List<OrderSummaryResponse> content, PageMetaResponse page) {
        this.content = content;
        this.page = page;
    }

    public List<OrderSummaryResponse> getContent() {
        return content;
    }

    public PageMetaResponse getPage() {
        return page;
    }
}
