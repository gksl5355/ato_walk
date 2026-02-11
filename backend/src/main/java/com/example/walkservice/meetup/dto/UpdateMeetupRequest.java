package com.example.walkservice.meetup.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
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

    private final DogSize dogSize;
    private final DogSociabilityLevel sociabilityLevel;
    private final DogReactivityLevel reactivityLevel;
    private final Boolean neutered;

    public UpdateMeetupRequest(
            String title,
            String description,
            String location,
            Integer maxParticipants,
            OffsetDateTime scheduledAt,
            DogSize dogSize,
            DogSociabilityLevel sociabilityLevel,
            DogReactivityLevel reactivityLevel,
            Boolean neutered
    ) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.maxParticipants = maxParticipants;
        this.scheduledAt = scheduledAt;
        this.dogSize = dogSize;
        this.sociabilityLevel = sociabilityLevel;
        this.reactivityLevel = reactivityLevel;
        this.neutered = neutered;
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
}
