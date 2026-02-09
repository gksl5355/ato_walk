package com.example.walkservice.communication.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.communication.dto.CommunicationResponse;
import com.example.walkservice.communication.dto.CreateCommunicationRequest;
import com.example.walkservice.communication.service.CommunicationService;
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
@RequestMapping("/api/v1/meetups/{meetupId}/communications")
public class CommunicationController {

    private final CommunicationService communicationService;
    private final CurrentUserProvider currentUserProvider;

    public CommunicationController(CommunicationService communicationService, CurrentUserProvider currentUserProvider) {
        this.communicationService = communicationService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public ApiResponse<CommunicationResponse> create(
            @PathVariable Long meetupId,
            @Valid @RequestBody CreateCommunicationRequest request
    ) {
        CommunicationResponse response = communicationService.createCommunication(currentUserId(), meetupId, request);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<Page<CommunicationResponse>> list(
            @PathVariable Long meetupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(communicationService.listCommunications(meetupId, pageable));
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
