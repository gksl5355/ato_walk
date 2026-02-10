package com.example.walkservice.user.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.dog.entity.Dog;
import com.example.walkservice.dog.repository.DogRepository;
import com.example.walkservice.user.dto.SignupRequest;
import com.example.walkservice.user.dto.UserResponse;
import com.example.walkservice.user.entity.User;
import com.example.walkservice.user.entity.UserStatus;
import com.example.walkservice.user.repository.UserRepository;
import java.time.OffsetDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, DogRepository dogRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.dogRepository = dogRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("USER_LOGIN_INVALID_CREDENTIALS", "Invalid email or password"));

        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new ApiException("USER_LOGIN_INVALID_CREDENTIALS", "Invalid email or password");
        }

        return toResponse(user);
    }

    public UserResponse signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ApiException("USER_SIGNUP_EMAIL_CONFLICT", "Email already exists");
        }

        OffsetDateTime now = OffsetDateTime.now();
        User savedUser = userRepository.save(new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                UserStatus.ACTIVE,
                now
        ));

        Dog dog = new Dog(
                savedUser.getId(),
                request.getDogName(),
                request.getDogBreed(),
                request.getDogSize(),
                request.getDogNeutered(),
                request.getDogSociabilityLevel(),
                request.getDogReactivityLevel(),
                request.getDogNotes(),
                now
        );
        dogRepository.save(dog);

        return toResponse(savedUser);
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
