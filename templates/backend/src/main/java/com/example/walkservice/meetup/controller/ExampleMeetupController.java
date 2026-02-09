package com.example.walkservice.meetup.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.meetup.dto.CreateExampleMeetupRequest;
import com.example.walkservice.meetup.dto.ExampleMeetupResponse;
import com.example.walkservice.meetup.service.ExampleMeetupService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/meetups")
public class ExampleMeetupController {

    private final ExampleMeetupService meetupService;

    public ExampleMeetupController(ExampleMeetupService meetupService) {
        this.meetupService = meetupService;
    }

    @PostMapping
    public ApiResponse<ExampleMeetupResponse> create(@Valid @RequestBody CreateExampleMeetupRequest request) {
        ExampleMeetupResponse response = meetupService.createMeetup(currentUserId(), request);
        return ApiResponse.success(response);
    }

    @PostMapping("/{meetupId}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long meetupId) {
        meetupService.cancelMeetup(currentUserId(), meetupId);
        return ApiResponse.success(null);
    }

    @GetMapping("/{meetupId}")
    public ApiResponse<ExampleMeetupResponse> get(@PathVariable Long meetupId) {
        return ApiResponse.success(meetupService.getMeetup(meetupId));
    }

    @GetMapping
    public ApiResponse<Page<ExampleMeetupResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(meetupService.listRecruitingMeetups(pageable));
    }

    private Long currentUserId() {
        throw new UnsupportedOperationException("Template: get user id from security context");
    }
}
