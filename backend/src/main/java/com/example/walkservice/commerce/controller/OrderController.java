package com.example.walkservice.commerce.controller;

import com.example.walkservice.commerce.dto.CreateOrderRequest;
import com.example.walkservice.commerce.dto.OrderResponse;
import com.example.walkservice.commerce.dto.OrderSummaryPageDataResponse;
import com.example.walkservice.commerce.service.OrderService;
import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider;

    public OrderController(OrderService orderService, CurrentUserProvider currentUserProvider) {
        this.orderService = orderService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    public ApiResponse<OrderResponse> createOrder(@RequestBody(required = false) CreateOrderRequest request) {
        return ApiResponse.success(orderService.createOrder(
                currentUserId(),
                request == null ? null : request.getPaymentMethod()
        ));
    }

    @GetMapping
    public ApiResponse<OrderSummaryPageDataResponse> listMyOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ApiResponse.success(orderService.listMyOrders(currentUserId(), pageable));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> getMyOrder(@PathVariable Long orderId) {
        return ApiResponse.success(orderService.getMyOrder(currentUserId(), orderId));
    }

    @PostMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ApiResponse.success(orderService.cancelOrder(currentUserId(), orderId));
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
