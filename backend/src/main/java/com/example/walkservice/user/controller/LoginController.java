package com.example.walkservice.user.controller;

import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.user.dto.LoginRequest;
import com.example.walkservice.user.dto.SignupRequest;
import com.example.walkservice.user.dto.UserResponse;
import com.example.walkservice.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class LoginController {

    static final String SESSION_USER_ID_KEY = "CURRENT_USER_ID";

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ApiResponse<UserResponse> signup(@Valid @RequestBody SignupRequest request, HttpSession session) {
        UserResponse user = userService.signup(request);
        authenticate(user, session);
        return ApiResponse.success(user);
    }

    @PostMapping("/login")
    public ApiResponse<UserResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        UserResponse user = userService.login(request.getEmail(), request.getPassword());
        authenticate(user, session);
        return ApiResponse.success(user);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        SecurityContextHolder.clearContext();
        session.invalidate();
        return ApiResponse.success(null);
    }

    private void authenticate(UserResponse user, HttpSession session) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user.getId(), null, List.of());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute(SESSION_USER_ID_KEY, user.getId());
    }
}
