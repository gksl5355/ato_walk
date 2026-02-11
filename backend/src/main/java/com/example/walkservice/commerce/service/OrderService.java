package com.example.walkservice.commerce.service;

import com.example.walkservice.commerce.dto.OrderItemResponse;
import com.example.walkservice.commerce.dto.OrderResponse;
import com.example.walkservice.commerce.dto.OrderSummaryPageDataResponse;
import com.example.walkservice.commerce.dto.OrderSummaryResponse;
import com.example.walkservice.commerce.dto.PageMetaResponse;
import com.example.walkservice.commerce.entity.CartItem;
import com.example.walkservice.commerce.entity.Order;
import com.example.walkservice.commerce.entity.OrderItem;
import com.example.walkservice.commerce.entity.OrderStatus;
import com.example.walkservice.commerce.entity.PaymentMethod;
import com.example.walkservice.commerce.entity.PointTransaction;
import com.example.walkservice.commerce.entity.PointTransactionType;
import com.example.walkservice.commerce.entity.Product;
import com.example.walkservice.commerce.entity.UserPointAccount;
import com.example.walkservice.commerce.repository.CartItemRepository;
import com.example.walkservice.commerce.repository.OrderItemRepository;
import com.example.walkservice.commerce.repository.OrderRepository;
import com.example.walkservice.commerce.repository.PointTransactionRepository;
import com.example.walkservice.commerce.repository.ProductRepository;
import com.example.walkservice.commerce.repository.UserPointAccountRepository;
import com.example.walkservice.common.exception.ApiException;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserPointAccountRepository userPointAccountRepository;
    private final PointTransactionRepository pointTransactionRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserPointAccountRepository userPointAccountRepository,
            PointTransactionRepository pointTransactionRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userPointAccountRepository = userPointAccountRepository;
        this.pointTransactionRepository = pointTransactionRepository;
    }

    public OrderResponse createOrder(Long actorUserId, PaymentMethod paymentMethod) {
        PaymentMethod actualPaymentMethod = paymentMethod == null ? PaymentMethod.POINT : paymentMethod;
        if (actualPaymentMethod != PaymentMethod.POINT) {
            throw new ApiException("ORDER_CREATE_PAYMENT_METHOD_UNSUPPORTED", "Only point payment is supported");
        }

        List<CartItem> cartItems = cartItemRepository.findAllByUserIdOrderByCreatedAtAsc(actorUserId);
        if (cartItems.isEmpty()) {
            throw new ApiException("ORDER_CREATE_CART_EMPTY", "Cart is empty");
        }

        Map<Long, Product> products = loadProductsForUpdate(cartItems.stream().map(CartItem::getProductId).collect(Collectors.toSet()));
        for (CartItem cartItem : cartItems) {
            Product product = requireProduct(products, cartItem.getProductId());
            if (!product.canPutInCart() || !product.hasStockFor(cartItem.getQuantity())) {
                throw new ApiException("ORDER_CREATE_STOCK_NOT_ENOUGH", "Insufficient stock for at least one item");
            }
        }

        long totalPrice = cartItems.stream()
                .mapToLong(item -> requireProduct(products, item.getProductId()).getPrice() * item.getQuantity())
                .sum();

        UserPointAccount account = userPointAccountRepository.findByIdForUpdate(actorUserId)
                .orElseThrow(() -> new ApiException("USER_FIND_NOT_FOUND", "User not found"));
        if (account.getPointBalance() < totalPrice) {
            throw new ApiException("ORDER_CREATE_POINT_INSUFFICIENT", "Insufficient point balance");
        }
        account.spendPoints(totalPrice);

        Order order = orderRepository.save(new Order(actorUserId, OrderStatus.CREATED, totalPrice, OffsetDateTime.now()));

        List<OrderItem> orderItems = cartItems.stream()
                .map(item -> {
                    Product product = requireProduct(products, item.getProductId());
                    long subtotal = product.getPrice() * item.getQuantity();
                    return new OrderItem(order.getId(), item.getProductId(), item.getQuantity(), product.getPrice(), subtotal);
                })
                .toList();

        List<OrderItem> savedOrderItems = orderItemRepository.saveAll(orderItems);

        for (OrderItem item : savedOrderItems) {
            Product product = requireProduct(products, item.getProductId());
            product.decreaseStock(item.getQuantity());
        }

        pointTransactionRepository.save(new PointTransaction(
                actorUserId,
                order.getId(),
                PointTransactionType.SPEND,
                totalPrice,
                account.getPointBalance(),
                OffsetDateTime.now()
        ));

        cartItemRepository.deleteAll(cartItems);
        return toOrderResponse(order, savedOrderItems);
    }

    @Transactional(readOnly = true)
    public OrderSummaryPageDataResponse listMyOrders(Long actorUserId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findAllByUserIdOrderByCreatedAtDesc(actorUserId, pageable);
        List<OrderSummaryResponse> content = orderPage.getContent().stream().map(this::toSummaryResponse).toList();
        return new OrderSummaryPageDataResponse(
                content,
                new PageMetaResponse(
                        orderPage.getNumber(),
                        orderPage.getSize(),
                        orderPage.getTotalElements(),
                        orderPage.getTotalPages()
                )
        );
    }

    @Transactional(readOnly = true)
    public OrderResponse getMyOrder(Long actorUserId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, actorUserId)
                .orElseThrow(() -> new ApiException("ORDER_FIND_NOT_FOUND", "Order not found"));
        List<OrderItem> items = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId());
        return toOrderResponse(order, items);
    }

    public OrderResponse cancelOrder(Long actorUserId, Long orderId) {
        Order order = orderRepository.findByIdAndUserId(orderId, actorUserId)
                .orElseThrow(() -> new ApiException("ORDER_FIND_NOT_FOUND", "Order not found"));

        order.cancel();

        List<OrderItem> items = orderItemRepository.findByOrderIdOrderByIdAsc(order.getId());
        Map<Long, Product> products = loadProductsForUpdate(items.stream().map(OrderItem::getProductId).collect(Collectors.toSet()));
        for (OrderItem item : items) {
            Product product = requireProduct(products, item.getProductId());
            product.increaseStock(item.getQuantity());
        }

        UserPointAccount account = userPointAccountRepository.findByIdForUpdate(actorUserId)
                .orElseThrow(() -> new ApiException("USER_FIND_NOT_FOUND", "User not found"));
        account.refundPoints(order.getTotalPrice());

        if (!pointTransactionRepository.existsByUserIdAndOrderIdAndType(actorUserId, order.getId(), PointTransactionType.REFUND)) {
            pointTransactionRepository.save(new PointTransaction(
                    actorUserId,
                    order.getId(),
                    PointTransactionType.REFUND,
                    order.getTotalPrice(),
                    account.getPointBalance(),
                    OffsetDateTime.now()
            ));
        }

        return toOrderResponse(order, items);
    }

    private Map<Long, Product> loadProductsForUpdate(Set<Long> productIds) {
        return productRepository.findAllByIdInForUpdate(productIds).stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));
    }

    private Product requireProduct(Map<Long, Product> products, Long productId) {
        Product product = products.get(productId);
        if (product == null) {
            throw new ApiException("PRODUCT_FIND_NOT_FOUND", "Product not found");
        }
        return product;
    }

    private OrderSummaryResponse toSummaryResponse(Order order) {
        return new OrderSummaryResponse(order.getId(), order.getStatus(), order.getTotalPrice(), order.getCreatedAt());
    }

    private OrderResponse toOrderResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> responses = items.stream()
                .map(item -> new OrderItemResponse(
                        item.getId(),
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotalPrice()
                ))
                .toList();

        return new OrderResponse(order.getId(), order.getStatus(), order.getTotalPrice(), order.getCreatedAt(), responses);
    }
}
