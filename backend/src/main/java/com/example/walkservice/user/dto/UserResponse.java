package com.example.walkservice.user.dto;

import com.example.walkservice.user.entity.UserStatus;
import java.time.OffsetDateTime;

public class UserResponse {

    private final Long id;
    private final String email;
    private final UserStatus status;
    private final OffsetDateTime createdAt;

    public UserResponse(Long id, String email, UserStatus status, OffsetDateTime createdAt) {
        this.id = id;
        this.email = email;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public UserStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
