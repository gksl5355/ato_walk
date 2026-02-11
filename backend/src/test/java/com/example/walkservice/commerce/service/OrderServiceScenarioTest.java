package com.example.walkservice.commerce.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
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
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceScenarioTest {

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
    void createThenCancelOrder_restoresStock() throws Exception {
        long actorUserId = 1L;

        CartItem cartItem = new CartItem(actorUserId, 10L, 2, OffsetDateTime.now());
        setId(cartItem, 100L);

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

        Order savedOrder = new Order(actorUserId, OrderStatus.CREATED, 64000L, OffsetDateTime.now());
        setId(savedOrder, 500L);

        OrderItem savedOrderItem = new OrderItem(500L, 10L, 2, 32000L, 64000L);
        setId(savedOrderItem, 700L);

        UserPointAccount account = createUserPointAccount();
        setId(account, actorUserId);
        setPointBalance(account, 200000L);

        when(cartItemRepository.findAllByUserIdOrderByCreatedAtAsc(actorUserId)).thenReturn(List.of(cartItem));
        when(productRepository.findAllByIdInForUpdate(any())).thenReturn(List.of(product));
        when(userPointAccountRepository.findByIdForUpdate(actorUserId)).thenReturn(Optional.of(account));
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        when(orderItemRepository.saveAll(anyList())).thenReturn(List.of(savedOrderItem));

        var created = orderService.createOrder(actorUserId, PaymentMethod.POINT);

        assertThat(created.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(created.getTotalPrice()).isEqualTo(64000L);
        assertThat(created.getItems()).hasSize(1);
        assertThat(product.getStockQuantity()).isEqualTo(8);
        assertThat(account.getPointBalance()).isEqualTo(136000L);

        when(orderRepository.findByIdAndUserId(500L, actorUserId)).thenReturn(Optional.of(savedOrder));
        when(orderItemRepository.findByOrderIdOrderByIdAsc(500L)).thenReturn(List.of(savedOrderItem));

        var canceled = orderService.cancelOrder(actorUserId, 500L);

        assertThat(canceled.getStatus()).isEqualTo(OrderStatus.CANCELED);
        assertThat(product.getStockQuantity()).isEqualTo(10);
        assertThat(account.getPointBalance()).isEqualTo(200000L);
    }

    private static void setId(Object target, Long id) throws Exception {
        Field field = target.getClass().getDeclaredField("id");
        field.setAccessible(true);
        field.set(target, id);
    }

    private static void setPointBalance(UserPointAccount account, Long pointBalance) throws Exception {
        Field field = UserPointAccount.class.getDeclaredField("pointBalance");
        field.setAccessible(true);
        field.set(account, pointBalance);
    }

    private static UserPointAccount createUserPointAccount() throws Exception {
        var constructor = UserPointAccount.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }
}
