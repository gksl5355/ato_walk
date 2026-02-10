package com.example.walkservice.participation.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.participation.dto.ParticipationResponse;
import com.example.walkservice.participation.service.ParticipationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meetups/{meetupId}/participations")
public class ParticipationController {

    private final ParticipationService participationService;
    private final CurrentUserProvider currentUserProvider;

    public ParticipationController(ParticipationService participationService, CurrentUserProvider currentUserProvider) {
        this.participationService = participationService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public ApiResponse<ParticipationResponse> request(@PathVariable Long meetupId) {
        ParticipationResponse response = participationService.requestParticipation(currentUserId(), meetupId);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<Page<ParticipationResponse>> listRequested(
            @PathVariable Long meetupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(participationService.listRequestedParticipations(currentUserId(), meetupId, pageable));
    }

    @PostMapping("/{participationId}/approve")
    public ApiResponse<ParticipationResponse> approve(
            @PathVariable Long meetupId,
            @PathVariable Long participationId
    ) {
        ParticipationResponse response = participationService.approveParticipation(currentUserId(), meetupId, participationId);
        return ApiResponse.success(response);
    }

    @PostMapping("/{participationId}/reject")
    public ApiResponse<ParticipationResponse> reject(
            @PathVariable Long meetupId,
            @PathVariable Long participationId
    ) {
        ParticipationResponse response = participationService.rejectParticipation(currentUserId(), meetupId, participationId);
        return ApiResponse.success(response);
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
