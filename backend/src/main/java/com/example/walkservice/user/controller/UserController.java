package com.example.walkservice.user.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.user.dto.UserResponse;
import com.example.walkservice.user.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserProvider currentUserProvider;

    public UserController(UserService userService, CurrentUserProvider currentUserProvider) {
        this.userService = userService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me() {
        return ApiResponse.success(userService.getMe(currentUserId()));
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
