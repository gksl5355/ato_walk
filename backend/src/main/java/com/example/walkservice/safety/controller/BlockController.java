package com.example.walkservice.safety.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.safety.dto.BlockResponse;
import com.example.walkservice.safety.dto.CreateBlockRequest;
import com.example.walkservice.safety.service.SafetyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/safety/blocks")
public class BlockController {

    private final SafetyService safetyService;
    private final CurrentUserProvider currentUserProvider;

    public BlockController(SafetyService safetyService, CurrentUserProvider currentUserProvider) {
        this.safetyService = safetyService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public ApiResponse<BlockResponse> create(@Valid @RequestBody CreateBlockRequest request) {
        BlockResponse response = safetyService.createBlock(currentUserId(), request);
        return ApiResponse.success(response);
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
