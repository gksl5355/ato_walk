package com.example.walkservice.safety.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.safety.dto.CreateReportRequest;
import com.example.walkservice.safety.dto.ReportResponse;
import com.example.walkservice.safety.service.SafetyService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/safety/reports")
public class ReportController {

    private final SafetyService safetyService;
    private final CurrentUserProvider currentUserProvider;

    public ReportController(SafetyService safetyService, CurrentUserProvider currentUserProvider) {
        this.safetyService = safetyService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public ApiResponse<ReportResponse> create(@Valid @RequestBody CreateReportRequest request) {
        ReportResponse response = safetyService.createReport(currentUserId(), request);
        return ApiResponse.success(response);
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
