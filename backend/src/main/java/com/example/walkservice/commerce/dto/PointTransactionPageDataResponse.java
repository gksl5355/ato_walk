package com.example.walkservice.commerce.dto;

import java.util.List;

public class PointTransactionPageDataResponse {

    private final List<PointTransactionResponse> content;
    private final PageMetaResponse page;

    public PointTransactionPageDataResponse(List<PointTransactionResponse> content, PageMetaResponse page) {
        this.content = content;
        this.page = page;
    }

    public List<PointTransactionResponse> getContent() {
        return content;
    }

    public PageMetaResponse getPage() {
        return page;
    }
}
