package com.example.walkservice.communication.dto;

import java.time.OffsetDateTime;

public class CommunicationResponse {

    private final Long id;
    private final Long meetupId;
    private final String content;
    private final OffsetDateTime createdAt;

    public CommunicationResponse(Long id, Long meetupId, String content, OffsetDateTime createdAt) {
        this.id = id;
        this.meetupId = meetupId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getMeetupId() {
        return meetupId;
    }

    public String getContent() {
        return content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
