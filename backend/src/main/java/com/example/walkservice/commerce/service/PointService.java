package com.example.walkservice.commerce.service;

import com.example.walkservice.commerce.dto.PageMetaResponse;
import com.example.walkservice.commerce.dto.PointTransactionPageDataResponse;
import com.example.walkservice.commerce.dto.PointTransactionResponse;
import com.example.walkservice.commerce.dto.UserPointsResponse;
import com.example.walkservice.commerce.entity.PointTransaction;
import com.example.walkservice.commerce.entity.UserPointAccount;
import com.example.walkservice.commerce.repository.PointTransactionRepository;
import com.example.walkservice.commerce.repository.UserPointAccountRepository;
import com.example.walkservice.common.exception.ApiException;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PointService {

    private final UserPointAccountRepository userPointAccountRepository;
    private final PointTransactionRepository pointTransactionRepository;

    public PointService(
            UserPointAccountRepository userPointAccountRepository,
            PointTransactionRepository pointTransactionRepository
    ) {
        this.userPointAccountRepository = userPointAccountRepository;
        this.pointTransactionRepository = pointTransactionRepository;
    }

    public UserPointsResponse getMyPoints(Long actorUserId) {
        UserPointAccount account = userPointAccountRepository.findById(actorUserId)
                .orElseThrow(() -> new ApiException("USER_FIND_NOT_FOUND", "User not found"));
        return new UserPointsResponse(account.getPointBalance());
    }

    public PointTransactionPageDataResponse listMyPointTransactions(Long actorUserId, Pageable pageable) {
        Page<PointTransaction> page = pointTransactionRepository.findAllByUserIdOrderByCreatedAtDesc(actorUserId, pageable);
        List<PointTransactionResponse> content = page.getContent().stream().map(this::toResponse).toList();
        return new PointTransactionPageDataResponse(
                content,
                new PageMetaResponse(page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages())
        );
    }

    private PointTransactionResponse toResponse(PointTransaction transaction) {
        return new PointTransactionResponse(
                transaction.getId(),
                transaction.getOrderId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getBalanceAfter(),
                transaction.getCreatedAt()
        );
    }
}
