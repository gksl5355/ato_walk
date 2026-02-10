package com.example.walkservice.participation.dto;

import com.example.walkservice.participation.entity.ParticipationStatus;
import java.time.OffsetDateTime;

public class ParticipationResponse {

    private final Long id;
    private final Long meetupId;
    private final Long userId;
    private final ParticipationStatus status;
    private final OffsetDateTime createdAt;

    public ParticipationResponse(
            Long id,
            Long meetupId,
            Long userId,
            ParticipationStatus status,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.meetupId = meetupId;
        this.userId = userId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getMeetupId() {
        return meetupId;
    }

    public Long getUserId() {
        return userId;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
