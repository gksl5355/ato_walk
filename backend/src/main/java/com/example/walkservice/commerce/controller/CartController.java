package com.example.walkservice.commerce.controller;

import com.example.walkservice.commerce.dto.CartItemResponse;
import com.example.walkservice.commerce.dto.CreateCartItemRequest;
import com.example.walkservice.commerce.dto.UpdateCartItemRequest;
import com.example.walkservice.commerce.service.CartService;
import com.example.walkservice.common.response.ApiResponse;
import com.example.walkservice.common.security.CurrentUserProvider;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart-items")
public class CartController {

    private final CartService cartService;
    private final CurrentUserProvider currentUserProvider;

    public CartController(CartService cartService, CurrentUserProvider currentUserProvider) {
        this.cartService = cartService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping
    public ApiResponse<List<CartItemResponse>> getMyCartItems() {
        return ApiResponse.success(cartService.getMyCartItems(currentUserId()));
    }

    @PostMapping
    public ApiResponse<CartItemResponse> addCartItem(@Valid @RequestBody CreateCartItemRequest request) {
        return ApiResponse.success(cartService.addCartItem(currentUserId(), request));
    }

    @PutMapping("/{cartItemId}")
    public ApiResponse<CartItemResponse> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return ApiResponse.success(cartService.updateCartItem(currentUserId(), cartItemId, request));
    }

    @DeleteMapping("/{cartItemId}")
    public ApiResponse<Void> deleteCartItem(@PathVariable Long cartItemId) {
        cartService.deleteCartItem(currentUserId(), cartItemId);
        return ApiResponse.success(null);
    }

    private Long currentUserId() {
        return currentUserProvider.currentUser().userId();
    }
}
