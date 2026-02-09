package com.example.walkservice.meetup.controller;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.meetup.dto.CreateMeetupRequest;
import com.example.walkservice.meetup.dto.MeetupResponse;
import com.example.walkservice.meetup.dto.UpdateMeetupRequest;
import com.example.walkservice.meetup.service.MeetupService;
import jakarta.validation.Valid;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meetups")
public class MeetupController {

    private static final Set<String> DOG_SIZES = Set.of("SMALL", "MEDIUM", "LARGE");
    private static final Set<String> LEVELS = Set.of("LOW", "MEDIUM", "HIGH");

    private final MeetupService meetupService;
    private final CurrentUserProvider currentUserProvider;

    public MeetupController(MeetupService meetupService, CurrentUserProvider currentUserProvider) {
        this.meetupService = meetupService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public ApiResponse<MeetupResponse> create(@Valid @RequestBody CreateMeetupRequest request) {
        MeetupResponse response = meetupService.createMeetup(currentUserId(), request);
        return ApiResponse.success(response);
    }

    @PutMapping("/{meetupId}")
    public ApiResponse<MeetupResponse> update(
            @PathVariable Long meetupId,
            @Valid @RequestBody UpdateMeetupRequest request
    ) {
        MeetupResponse response = meetupService.updateMeetup(currentUserId(), meetupId, request);
        return ApiResponse.success(response);
    }

    @PostMapping("/{meetupId}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long meetupId) {
        meetupService.cancelMeetup(currentUserId(), meetupId);
        return ApiResponse.success(null);
    }

    @PostMapping("/{meetupId}/end")
    public ApiResponse<Void> end(@PathVariable Long meetupId) {
        meetupService.endMeetup(currentUserId(), meetupId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{meetupId}")
    public ApiResponse<MeetupResponse> get(@PathVariable Long meetupId) {
        return ApiResponse.success(meetupService.getMeetup(meetupId));
    }

    @GetMapping
    public ApiResponse<Page<MeetupResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String dogSize,
            @RequestParam(required = false) String sociabilityLevel,
            @RequestParam(required = false) String reactivityLevel
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(
                meetupService.listRecruitingMeetups(
                        pageable,
                        normalizeDogSize(dogSize),
                        normalizeLevel("sociabilityLevel", sociabilityLevel),
                        normalizeLevel("reactivityLevel", reactivityLevel)
                )
        );
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }

    private String normalizeDogSize(String value) {
        String normalized = normalizeOptionalEnumValue(value);
        if (normalized == null) {
            return null;
        }
        if (!DOG_SIZES.contains(normalized)) {
            throw new ApiException("MEETUP_LIST_INVALID_FILTER", "Invalid dogSize");
        }
        return normalized;
    }

    private String normalizeLevel(String field, String value) {
        String normalized = normalizeOptionalEnumValue(value);
        if (normalized == null) {
            return null;
        }
        if (!LEVELS.contains(normalized)) {
            throw new ApiException("MEETUP_LIST_INVALID_FILTER", "Invalid " + field);
        }
        return normalized;
    }

    private String normalizeOptionalEnumValue(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        return trimmed.toUpperCase();
    }
}
