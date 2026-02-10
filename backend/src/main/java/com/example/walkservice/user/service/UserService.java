package com.example.walkservice.user.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.user.dto.UserResponse;
import com.example.walkservice.user.entity.User;
import com.example.walkservice.user.entity.UserStatus;
import com.example.walkservice.user.repository.UserRepository;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse login(String email) {
        User user = userRepository.findByEmail(email)
                .orElseGet(() -> userRepository.save(new User(email, UserStatus.ACTIVE, OffsetDateTime.now())));
        return toResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getMe(Long actorUserId) {
        User user = userRepository.findById(actorUserId)
                .orElseThrow(() -> new ApiException("USER_FIND_NOT_FOUND", "User not found"));
        return toResponse(user);
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getEmail(), user.getStatus(), user.getCreatedAt());
    }
}
