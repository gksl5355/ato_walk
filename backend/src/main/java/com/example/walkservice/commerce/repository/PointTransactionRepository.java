package com.example.walkservice.commerce.repository;

import com.example.walkservice.commerce.entity.PointTransaction;
import com.example.walkservice.commerce.entity.PointTransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {
    Page<PointTransaction> findAllByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);

    boolean existsByUserIdAndOrderIdAndType(Long userId, Long orderId, PointTransactionType type);
}
