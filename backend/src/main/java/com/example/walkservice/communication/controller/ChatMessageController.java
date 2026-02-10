package com.example.walkservice.communication.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.communication.dto.ChatMessageResponse;
import com.example.walkservice.communication.dto.CreateChatMessageRequest;
import com.example.walkservice.communication.service.ChatMessageService;
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
@RequestMapping("/api/v1/meetups/{meetupId}/chats")
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final CurrentUserProvider currentUserProvider;

    public ChatMessageController(ChatMessageService chatMessageService, CurrentUserProvider currentUserProvider) {
        this.chatMessageService = chatMessageService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public ApiResponse<ChatMessageResponse> create(
            @PathVariable Long meetupId,
            @Valid @RequestBody CreateChatMessageRequest request
    ) {
        ChatMessageResponse response = chatMessageService.createMessage(currentUserId(), meetupId, request);
        return ApiResponse.success(response);
    }

    @GetMapping
    public ApiResponse<Page<ChatMessageResponse>> list(
            @PathVariable Long meetupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(chatMessageService.listMessages(currentUserId(), meetupId, pageable));
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
