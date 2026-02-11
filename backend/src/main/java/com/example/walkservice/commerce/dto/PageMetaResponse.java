package com.example.walkservice.commerce.dto;

public class PageMetaResponse {

    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;

    public PageMetaResponse(int page, int size, long totalElements, int totalPages) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }
}
