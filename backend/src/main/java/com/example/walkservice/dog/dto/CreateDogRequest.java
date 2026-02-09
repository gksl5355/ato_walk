package com.example.walkservice.dog.dto;

import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateDogRequest {

    @NotBlank
    private final String name;

    @NotBlank
    private final String breed;

    @NotNull
    private final DogSize size;

    @NotNull
    private final Boolean neutered;

    @NotNull
    private final DogSociabilityLevel sociabilityLevel;

    @NotNull
    private final DogReactivityLevel reactivityLevel;

    private final String notes;

    public CreateDogRequest(
            String name,
            String breed,
            DogSize size,
            Boolean neutered,
            DogSociabilityLevel sociabilityLevel,
            DogReactivityLevel reactivityLevel,
            String notes
    ) {
        this.name = name;
        this.breed = breed;
        this.size = size;
        this.neutered = neutered;
        this.sociabilityLevel = sociabilityLevel;
        this.reactivityLevel = reactivityLevel;
        this.notes = notes;
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
}
