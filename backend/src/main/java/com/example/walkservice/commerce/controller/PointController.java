package com.example.walkservice.commerce.controller;

import com.example.walkservice.commerce.dto.PointTransactionPageDataResponse;
import com.example.walkservice.commerce.dto.UserPointsResponse;
import com.example.walkservice.commerce.service.PointService;
import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PointController {

    private final PointService pointService;
    private final CurrentUserProvider currentUserProvider;

    public PointController(PointService pointService, CurrentUserProvider currentUserProvider) {
        this.pointService = pointService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/api/v1/users/me/points")
    public ApiResponse<UserPointsResponse> getMyPoints() {
        return ApiResponse.success(pointService.getMyPoints(currentUserId()));
    }

    @GetMapping("/api/v1/points/transactions")
    public ApiResponse<PointTransactionPageDataResponse> listMyPointTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(pointService.listMyPointTransactions(currentUserId(), pageable));
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
