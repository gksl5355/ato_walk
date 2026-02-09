package com.example.walkservice.dog.dto;

import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import java.time.OffsetDateTime;

public class DogResponse {

    private final Long id;
    private final Long userId;
    private final String name;
    private final String breed;
    private final DogSize size;
    private final Boolean neutered;
    private final DogSociabilityLevel sociabilityLevel;
    private final DogReactivityLevel reactivityLevel;
    private final String notes;
    private final OffsetDateTime createdAt;

    public DogResponse(
            Long id,
            Long userId,
            String name,
            String breed,
            DogSize size,
            Boolean neutered,
            DogSociabilityLevel sociabilityLevel,
            DogReactivityLevel reactivityLevel,
            String notes,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.breed = breed;
        this.size = size;
        this.neutered = neutered;
        this.sociabilityLevel = sociabilityLevel;
        this.reactivityLevel = reactivityLevel;
        this.notes = notes;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public String getBreed() {
        return breed;
    }

    public DogSize getSize() {
        return size;
    }

    public Boolean getNeutered() {
        return neutered;
    }

    public DogSociabilityLevel getSociabilityLevel() {
        return sociabilityLevel;
    }

    public DogReactivityLevel getReactivityLevel() {
        return reactivityLevel;
    }

    public String getNotes() {
        return notes;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
