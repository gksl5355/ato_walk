package com.example.walkservice.safety.dto;

import java.time.OffsetDateTime;

public class BlockResponse {

    private final Long id;
    private final Long blockerUserId;
    private final Long blockedUserId;
    private final OffsetDateTime createdAt;

    public BlockResponse(Long id, Long blockerUserId, Long blockedUserId, OffsetDateTime createdAt) {
        this.id = id;
        this.blockerUserId = blockerUserId;
        this.blockedUserId = blockedUserId;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getBlockerUserId() {
        return blockerUserId;
    }

    public Long getBlockedUserId() {
        return blockedUserId;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
