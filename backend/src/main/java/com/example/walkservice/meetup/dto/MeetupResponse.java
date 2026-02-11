package com.example.walkservice.meetup.dto;

import com.example.walkservice.meetup.entity.MeetupStatus;
import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import java.time.OffsetDateTime;

public class MeetupResponse {

    private final Long id;
    private final Long hostUserId;
    private final String title;
    private final String description;
    private final String location;
    private final Integer maxParticipants;
    private final OffsetDateTime scheduledAt;
    private final DogSize dogSize;
    private final DogSociabilityLevel sociabilityLevel;
    private final DogReactivityLevel reactivityLevel;
    private final Boolean neutered;
    private final MeetupStatus status;

    public MeetupResponse(
            Long id,
            Long hostUserId,
            String title,
            String description,
            String location,
            Integer maxParticipants,
            OffsetDateTime scheduledAt,
            DogSize dogSize,
            DogSociabilityLevel sociabilityLevel,
            DogReactivityLevel reactivityLevel,
            Boolean neutered,
            MeetupStatus status
    ) {
        this.id = id;
        this.hostUserId = hostUserId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.scheduledAt = scheduledAt;
        this.dogSize = dogSize;
        this.sociabilityLevel = sociabilityLevel;
        this.reactivityLevel = reactivityLevel;
        this.neutered = neutered;
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

    public DogSize getDogSize() {
        return dogSize;
    }

    public DogSociabilityLevel getSociabilityLevel() {
        return sociabilityLevel;
    }

    public DogReactivityLevel getReactivityLevel() {
        return reactivityLevel;
    }

    public Boolean getNeutered() {
        return neutered;
    }

    public MeetupStatus getStatus() {
        return status;
    }
}
