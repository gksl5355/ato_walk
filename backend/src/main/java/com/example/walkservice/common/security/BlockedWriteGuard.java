package com.example.walkservice.common.security;

import com.example.walkservice.common.exception.ApiException;
import org.springframework.stereotype.Component;

@Component
public class BlockedWriteGuard {

    private static final String USER_STATUS_BLOCKED = "BLOCKED";

    private final UserStatusLookupRepository userStatusLookupRepository;

    public BlockedWriteGuard(UserStatusLookupRepository userStatusLookupRepository) {
        this.userStatusLookupRepository = userStatusLookupRepository;
    }

    public void ensureNotBlocked(Long actorUserId, String forbiddenCode) {
        String status = userStatusLookupRepository.findStatusByUserId(actorUserId);
        if (USER_STATUS_BLOCKED.equals(status)) {
            throw new ApiException(forbiddenCode, "Blocked user cannot perform write actions");
        }
    }
}
