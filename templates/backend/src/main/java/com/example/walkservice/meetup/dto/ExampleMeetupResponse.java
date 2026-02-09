package com.example.walkservice.meetup.dto;

import com.example.walkservice.meetup.entity.ExampleMeetupStatus;
import java.time.OffsetDateTime;

public class ExampleMeetupResponse {

    private final Long id;
    private final Long hostUserId;
    private final String title;
    private final String description;
    private final String location;
    private final Integer maxParticipants;
    private final OffsetDateTime scheduledAt;
    private final ExampleMeetupStatus status;

    public ExampleMeetupResponse(
            Long id,
            Long hostUserId,
            String title,
            String description,
            String location,
            Integer maxParticipants,
            OffsetDateTime scheduledAt,
            ExampleMeetupStatus status
    ) {
        this.id = id;
        this.hostUserId = hostUserId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.scheduledAt = scheduledAt;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getHostUserId() {
        return hostUserId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Integer getMaxParticipants() {
        return maxParticipants;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    public ExampleMeetupStatus getStatus() {
        return status;
    }
}
