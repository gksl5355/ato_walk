package com.example.walkservice.meetup.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

public class UpdateMeetupRequest {

    @NotBlank
    private final String title;

    private final String description;

    @NotBlank
    private final String location;

    @NotNull
    @Min(1)
    private final Integer maxParticipants;

    @NotNull
    private final OffsetDateTime scheduledAt;

    public UpdateMeetupRequest(
            String title,
            String description,
            String location,
            Integer maxParticipants,
            OffsetDateTime scheduledAt
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.scheduledAt = scheduledAt;
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
}
