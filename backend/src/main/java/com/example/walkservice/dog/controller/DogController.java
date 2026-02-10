package com.example.walkservice.dog.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.dog.dto.CreateDogRequest;
import com.example.walkservice.dog.dto.DogResponse;
import com.example.walkservice.dog.dto.UpdateDogRequest;
import com.example.walkservice.dog.service.DogService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/{dogId}")
    public ApiResponse<DogResponse> get(@PathVariable Long dogId) {
        DogResponse response = dogService.getDog(currentUserId(), dogId);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<Page<DogResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DogResponse> response = dogService.listDogs(currentUserId(), pageable);
        return ApiResponse.success(response);
    }

    @DeleteMapping("/{dogId}")
    public ApiResponse<Void> delete(@PathVariable Long dogId) {
        dogService.deleteDog(currentUserId(), dogId);
        return ApiResponse.success(null);
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
