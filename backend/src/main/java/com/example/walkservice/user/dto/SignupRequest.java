package com.example.walkservice.user.dto;

import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class SignupRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String dogName;

    @NotBlank
    private String dogBreed;

    @NotNull
    private DogSize dogSize;

    @NotNull
    private Boolean dogNeutered;

    @NotNull
    private DogSociabilityLevel dogSociabilityLevel;

    @NotNull
    private DogReactivityLevel dogReactivityLevel;

    private String dogNotes;

    protected SignupRequest() {
    }

    public SignupRequest(
            String email,
            String password,
            String dogName,
            String dogBreed,
            DogSize dogSize,
            Boolean dogNeutered,
            DogSociabilityLevel dogSociabilityLevel,
            DogReactivityLevel dogReactivityLevel,
            String dogNotes
    ) {
        this.email = email;
        this.password = password;
        this.dogName = dogName;
        this.dogBreed = dogBreed;
        this.dogSize = dogSize;
        this.dogNeutered = dogNeutered;
        this.dogSociabilityLevel = dogSociabilityLevel;
        this.dogReactivityLevel = dogReactivityLevel;
        this.dogNotes = dogNotes;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getDogName() {
        return dogName;
    }

    public String getDogBreed() {
        return dogBreed;
    }

    public DogSize getDogSize() {
        return dogSize;
    }

    public Boolean getDogNeutered() {
        return dogNeutered;
    }

    public DogSociabilityLevel getDogSociabilityLevel() {
        return dogSociabilityLevel;
    }

    public DogReactivityLevel getDogReactivityLevel() {
        return dogReactivityLevel;
    }

    public String getDogNotes() {
        return dogNotes;
    }
}
