package com.example.walkservice.safety.dto;

import jakarta.validation.constraints.NotNull;

public class CreateBlockRequest {

    @NotNull
    private Long blockedUserId;

    protected CreateBlockRequest() {
    }

    public CreateBlockRequest(Long blockedUserId) {
        this.blockedUserId = blockedUserId;
    }

    public Long getBlockedUserId() {
        return blockedUserId;
    }
}
