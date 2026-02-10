package com.example.walkservice.common.security;

import com.example.walkservice.common.exception.ApiException;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SessionCurrentUserProvider implements CurrentUserProvider {

    private static final String SESSION_USER_ID_KEY = "CURRENT_USER_ID";

    private final ObjectProvider<HttpSession> httpSessionProvider;

    public SessionCurrentUserProvider(ObjectProvider<HttpSession> httpSessionProvider) {
        this.httpSessionProvider = httpSessionProvider;
    }

    @Override
    public CurrentUser currentUser() {
        Long userId = readUserIdFromSecurityContext();
        if (userId == null) {
            userId = readUserIdFromSession();
        }

        if (userId == null) {
            throw new ApiException("COMMON_AUTH_REQUIRED", "Authentication required");
        }

        return new CurrentUser(userId);
    }

    private Long readUserIdFromSecurityContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Long id) {
            return id;
        }
        if (principal instanceof String id) {
            try {
                return Long.parseLong(id);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }

    private Long readUserIdFromSession() {
        HttpSession session = httpSessionProvider.getIfAvailable();
        if (session == null) {
            return null;
        }

        Object value = session.getAttribute(SESSION_USER_ID_KEY);
        if (value instanceof Long id) {
            return id;
        }
        if (value instanceof String id) {
            try {
                return Long.parseLong(id);
            } catch (NumberFormatException ex) {
                return null;
            }
        }
        return null;
    }
}
