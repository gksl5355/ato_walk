package com.example.walkservice.communication.dto;

import java.time.OffsetDateTime;

public class ChatMessageResponse {

    private final Long id;
    private final Long meetupId;
    private final Long senderUserId;
    private final String senderNickname;
    private final String content;
    private final OffsetDateTime createdAt;

    public ChatMessageResponse(
            Long id,
            Long meetupId,
            Long senderUserId,
            String senderNickname,
            String content,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.meetupId = meetupId;
        this.senderUserId = senderUserId;
        this.senderNickname = senderNickname;
        this.content = content;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getMeetupId() {
        return meetupId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public String getContent() {
        return content;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
