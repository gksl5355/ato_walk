package com.example.walkservice.commerce.service;

import com.example.walkservice.commerce.dto.CartItemResponse;
import com.example.walkservice.commerce.dto.CreateCartItemRequest;
import com.example.walkservice.commerce.dto.UpdateCartItemRequest;
import com.example.walkservice.commerce.entity.CartItem;
import com.example.walkservice.commerce.entity.Product;
import com.example.walkservice.commerce.repository.CartItemRepository;
import com.example.walkservice.commerce.repository.ProductRepository;
import com.example.walkservice.common.exception.ApiException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository, ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getMyCartItems(Long actorUserId) {
        List<CartItem> cartItems = cartItemRepository.findAllByUserIdOrderByCreatedAtAsc(actorUserId);
        if (cartItems.isEmpty()) {
            return List.of();
        }

        Map<Long, Product> products = loadProductsByIds(cartItems.stream().map(CartItem::getProductId).collect(Collectors.toSet()));

        return cartItems.stream().map(item -> toResponse(item, requireProduct(products, item.getProductId()))).toList();
    }

    public CartItemResponse addCartItem(Long actorUserId, CreateCartItemRequest request) {
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ApiException("PRODUCT_FIND_NOT_FOUND", "Product not found"));

        validateCartQuantity(product, request.getQuantity());

        CartItem cartItem = cartItemRepository.findByUserIdAndProductId(actorUserId, request.getProductId())
                .map(existing -> {
                    existing.updateQuantity(request.getQuantity());
                    return existing;
                })
                .orElseGet(() -> new CartItem(actorUserId, request.getProductId(), request.getQuantity(), OffsetDateTime.now()));

        CartItem saved = cartItemRepository.save(cartItem);
        return toResponse(saved, product);
    }

    public CartItemResponse updateCartItem(Long actorUserId, Long cartItemId, UpdateCartItemRequest request) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, actorUserId)
                .orElseThrow(() -> new ApiException("CART_FIND_NOT_FOUND", "Cart item not found"));

        Product product = productRepository.findById(cartItem.getProductId())
                .orElseThrow(() -> new ApiException("PRODUCT_FIND_NOT_FOUND", "Product not found"));

        validateCartQuantity(product, request.getQuantity());
        cartItem.updateQuantity(request.getQuantity());
        return toResponse(cartItem, product);
    }

    public void deleteCartItem(Long actorUserId, Long cartItemId) {
        CartItem cartItem = cartItemRepository.findByIdAndUserId(cartItemId, actorUserId)
                .orElseThrow(() -> new ApiException("CART_FIND_NOT_FOUND", "Cart item not found"));
        cartItemRepository.delete(cartItem);
    }

    private Map<Long, Product> loadProductsByIds(Set<Long> productIds) {
        return productRepository.findAllByIdIn(productIds).stream().collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    private Product requireProduct(Map<Long, Product> products, Long productId) {
        Product product = products.get(productId);
        if (product == null) {
            throw new ApiException("PRODUCT_FIND_NOT_FOUND", "Product not found");
        }
        return product;
    }

    private void validateCartQuantity(Product product, Integer quantity) {
        if (!product.canPutInCart()) {
            throw new ApiException("CART_ADD_SOLD_OUT", "Sold out product cannot be added to cart");
        }
        if (!product.hasStockFor(quantity)) {
            throw new ApiException("CART_ADD_OUT_OF_STOCK", "Insufficient stock");
        }
    }

    private CartItemResponse toResponse(CartItem item, Product product) {
        long subtotalPrice = product.getPrice() * item.getQuantity();
        return new CartItemResponse(
                item.getId(),
                item.getProductId(),
                product.getName(),
                item.getQuantity(),
                product.getPrice(),
                subtotalPrice
        );
    }
}
