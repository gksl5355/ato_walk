package com.example.walkservice.dog.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.dog.dto.CreateDogRequest;
import com.example.walkservice.dog.dto.DogResponse;
import com.example.walkservice.dog.dto.UpdateDogRequest;
import com.example.walkservice.dog.service.DogService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dogs")
public class DogController {

    private final DogService dogService;
    private final CurrentUserProvider currentUserProvider;

    public DogController(DogService dogService, CurrentUserProvider currentUserProvider) {
        this.dogService = dogService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public ApiResponse<DogResponse> create(@Valid @RequestBody CreateDogRequest request) {
        DogResponse response = dogService.createDog(currentUserId(), request);
        return ApiResponse.success(response);
    }

    @PutMapping("/{dogId}")
    public ApiResponse<DogResponse> update(@PathVariable Long dogId, @Valid @RequestBody UpdateDogRequest request) {
        DogResponse response = dogService.updateDog(currentUserId(), dogId, request);
        return ApiResponse.success(response);
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
