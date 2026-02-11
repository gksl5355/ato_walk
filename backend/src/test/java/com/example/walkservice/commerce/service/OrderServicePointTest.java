package com.example.walkservice.commerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.walkservice.commerce.entity.CartItem;
import com.example.walkservice.commerce.entity.Order;
import com.example.walkservice.commerce.entity.OrderItem;
import com.example.walkservice.commerce.entity.OrderStatus;
import com.example.walkservice.commerce.entity.PaymentMethod;
import com.example.walkservice.commerce.entity.Product;
import com.example.walkservice.commerce.entity.ProductCategory;
import com.example.walkservice.commerce.entity.ProductStatus;
import com.example.walkservice.commerce.entity.UserPointAccount;
import com.example.walkservice.commerce.repository.CartItemRepository;
import com.example.walkservice.commerce.repository.OrderItemRepository;
import com.example.walkservice.commerce.repository.OrderRepository;
import com.example.walkservice.commerce.repository.PointTransactionRepository;
import com.example.walkservice.commerce.repository.ProductRepository;
import com.example.walkservice.commerce.repository.UserPointAccountRepository;
import com.example.walkservice.common.exception.ApiException;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServicePointTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserPointAccountRepository userPointAccountRepository;

    @Mock
    private PointTransactionRepository pointTransactionRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_insufficientPoints_failsWithoutStockChange() throws Exception {
        long actorUserId = 1L;
        CartItem cartItem = new CartItem(actorUserId, 10L, 2, OffsetDateTime.now());

        Product product = new Product(
                "튼튼 사료 2kg",
                ProductCategory.FEED,
                32000L,
                10,
                ProductStatus.ON_SALE,
                "성견용 균형 영양 사료",
                OffsetDateTime.now().minusDays(1)
        );
        setId(product, 10L);

        UserPointAccount account = createUserPointAccount();
        setId(account, actorUserId);
        setPointBalance(account, 1000L);

        when(cartItemRepository.findAllByUserIdOrderByCreatedAtAsc(actorUserId)).thenReturn(List.of(cartItem));
        when(productRepository.findAllByIdInForUpdate(any())).thenReturn(List.of(product));
        when(userPointAccountRepository.findByIdForUpdate(actorUserId)).thenReturn(Optional.of(account));

        assertThatThrownBy(() -> orderService.createOrder(actorUserId, PaymentMethod.POINT))
                .isInstanceOf(ApiException.class)
                .hasMessage("Insufficient point balance");

        assertThat(product.getStockQuantity()).isEqualTo(10);
        assertThat(account.getPointBalance()).isEqualTo(1000L);
        verify(orderRepository, never()).save(any(Order.class));
        verify(orderItemRepository, never()).saveAll(anyList());
    }

    @Test
    void createOrder_defaultsToPointWhenRequestBodyMissing() throws Exception {
        long actorUserId = 1L;

        CartItem cartItem = new CartItem(actorUserId, 10L, 1, OffsetDateTime.now());
        Product product = new Product(
                "노즈워크 장난감 볼",
                ProductCategory.TOY,
                15000L,
                8,
                ProductStatus.ON_SALE,
                "후각 놀이용 퍼즐 토이",
                OffsetDateTime.now().minusDays(1)
        );
        setId(product, 10L);

        UserPointAccount account = createUserPointAccount();
        setId(account, actorUserId);
        setPointBalance(account, 30000L);

        Order savedOrder = new Order(actorUserId, OrderStatus.CREATED, 15000L, OffsetDateTime.now());
        setId(savedOrder, 500L);
        OrderItem savedOrderItem = new OrderItem(500L, 10L, 1, 15000L, 15000L);

        when(cartItemRepository.findAllByUserIdOrderByCreatedAtAsc(actorUserId)).thenReturn(List.of(cartItem));
        when(productRepository.findAllByIdInForUpdate(any())).thenReturn(List.of(product));
        when(userPointAccountRepository.findByIdForUpdate(actorUserId)).thenReturn(Optional.of(account));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.saveAll(anyList())).thenReturn(List.of(savedOrderItem));

        var created = orderService.createOrder(actorUserId, null);

        assertThat(created.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(account.getPointBalance()).isEqualTo(15000L);
    }

    @Test
    void concurrentCreateOrder_preventsOverspend() throws Exception {
        long actorUserId = 1L;
        UserPointAccount sharedAccount = new SynchronizedUserPointAccount();
        setId(sharedAccount, actorUserId);
        setPointBalance(sharedAccount, 5000L);

        CartItem cartItem = new CartItem(actorUserId, 10L, 1, OffsetDateTime.now());
        Product product = new Product(
                "바삭 닭가슴살 간식",
                ProductCategory.SNACK,
                4000L,
                10,
                ProductStatus.ON_SALE,
                "훈련 보상용 저염 간식",
                OffsetDateTime.now().minusDays(1)
        );
        setId(product, 10L);

        Order savedOrder = new Order(actorUserId, OrderStatus.CREATED, 4000L, OffsetDateTime.now());
        setId(savedOrder, 900L);
        OrderItem savedOrderItem = new OrderItem(900L, 10L, 1, 4000L, 4000L);

        when(cartItemRepository.findAllByUserIdOrderByCreatedAtAsc(actorUserId)).thenReturn(List.of(cartItem));
        when(productRepository.findAllByIdInForUpdate(any())).thenReturn(List.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.saveAll(anyList())).thenReturn(List.of(savedOrderItem));

        when(userPointAccountRepository.findByIdForUpdate(actorUserId)).thenReturn(Optional.of(sharedAccount));

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(2);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        Runnable task = () -> {
            try {
                orderService.createOrder(actorUserId, PaymentMethod.POINT);
                successCount.incrementAndGet();
            } catch (ApiException ex) {
                if ("ORDER_CREATE_POINT_INSUFFICIENT".equals(ex.getCode())) {
                    failCount.incrementAndGet();
                }
            } finally {
                latch.countDown();
            }
        };

        executor.submit(task);
        executor.submit(task);
        latch.await();
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(1);
        assertThat(sharedAccount.getPointBalance()).isEqualTo(1000L);
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    private static void setId(Object target, Long id) throws Exception {
        Field field = findField(target.getClass(), "id");
        field.setAccessible(true);
        field.set(target, id);
    }

    private static void setPointBalance(UserPointAccount account, Long pointBalance) throws Exception {
        Field field = findField(account.getClass(), "pointBalance");
        field.setAccessible(true);
        field.set(account, pointBalance);
    }

    private static Field findField(Class<?> type, String fieldName) throws NoSuchFieldException {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new NoSuchFieldException(fieldName);
    }

    private static UserPointAccount createUserPointAccount() throws Exception {
        var constructor = UserPointAccount.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private static class SynchronizedUserPointAccount extends UserPointAccount {
        @Override
        public synchronized void spendPoints(long amount) {
            super.spendPoints(amount);
        }

        @Override
        public synchronized void refundPoints(long amount) {
            super.refundPoints(amount);
        }
    }
}
