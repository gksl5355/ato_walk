package com.example.walkservice.commerce.repository;

import com.example.walkservice.commerce.entity.OrderItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findByOrderIdOrderByIdAsc(Long orderId);
}
