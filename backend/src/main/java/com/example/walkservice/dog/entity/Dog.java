package com.example.walkservice.dog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "dogs")
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "breed", nullable = false)
    private String breed;

    @Enumerated(EnumType.STRING)
    @Column(name = "size", nullable = false)
    private DogSize size;

    @Column(name = "neutered", nullable = false)
    private Boolean neutered;

    @Enumerated(EnumType.STRING)
    @Column(name = "sociability_level", nullable = false)
    private DogSociabilityLevel sociabilityLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "reactivity_level", nullable = false)
    private DogReactivityLevel reactivityLevel;

    @Column(name = "notes")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected Dog() {
    }

    public Dog(
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

    public void updateProfile(
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
