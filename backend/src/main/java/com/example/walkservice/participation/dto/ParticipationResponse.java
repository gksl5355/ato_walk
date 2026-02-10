package com.example.walkservice.participation.dto;

import com.example.walkservice.participation.entity.ParticipationStatus;
import java.time.OffsetDateTime;

public class ParticipationResponse {

    private final Long id;
    private final Long meetupId;
    private final Long userId;
    private final String userNickname;
    private final ParticipationStatus status;
    private final OffsetDateTime createdAt;

    public ParticipationResponse(
            Long id,
            Long meetupId,
            Long userId,
            String userNickname,
            ParticipationStatus status,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.meetupId = meetupId;
        this.userId = userId;
        this.userNickname = userNickname;
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

    public String getUserNickname() {
        return userNickname;
    }

    public ParticipationStatus getStatus() {
        return status;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
