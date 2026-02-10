package com.example.walkservice.dog.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.dog.dto.CreateDogRequest;
import com.example.walkservice.dog.dto.DogResponse;
import com.example.walkservice.dog.dto.UpdateDogRequest;
import com.example.walkservice.dog.entity.Dog;
import com.example.walkservice.dog.repository.DogRepository;
import com.example.walkservice.dog.repository.UserStatusLookupRepository;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DogService {

    private static final String USER_STATUS_BLOCKED = "BLOCKED";

    private final DogRepository dogRepository;
    private final UserStatusLookupRepository userStatusLookupRepository;

    public DogService(DogRepository dogRepository, UserStatusLookupRepository userStatusLookupRepository) {
        this.dogRepository = dogRepository;
        this.userStatusLookupRepository = userStatusLookupRepository;
    }

    public DogResponse createDog(Long actorUserId, CreateDogRequest request) {
        ensureActorNotBlocked(actorUserId, "DOG_CREATE_FORBIDDEN");

        Dog dog = new Dog(
                actorUserId,
                request.getName(),
                request.getBreed(),
                request.getSize(),
                request.getNeutered(),
                request.getSociabilityLevel(),
                request.getReactivityLevel(),
                request.getNotes(),
                OffsetDateTime.now()
        );

        Dog saved = dogRepository.save(dog);
        return toResponse(saved);
    }

    public DogResponse updateDog(Long actorUserId, Long dogId, UpdateDogRequest request) {
        ensureActorNotBlocked(actorUserId, "DOG_UPDATE_FORBIDDEN");

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new ApiException("DOG_FIND_NOT_FOUND", "Dog not found"));

        if (!dog.getUserId().equals(actorUserId)) {
            throw new ApiException("DOG_UPDATE_FORBIDDEN", "Only owner can update dog");
        }

        dog.updateProfile(
                request.getName(),
                request.getBreed(),
                request.getSize(),
                request.getNeutered(),
                request.getSociabilityLevel(),
                request.getReactivityLevel(),
                request.getNotes()
        );

        return toResponse(dog);
    }

    @Transactional(readOnly = true)
    public DogResponse getDog(Long actorUserId, Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new ApiException("DOG_FIND_NOT_FOUND", "Dog not found"));

        if (!dog.getUserId().equals(actorUserId)) {
            throw new ApiException("DOG_GET_FORBIDDEN", "Only owner can access dog");
        }

        return toResponse(dog);
    }

    @Transactional(readOnly = true)
    public Page<DogResponse> listDogs(Long actorUserId, Pageable pageable) {
        return dogRepository.findAllByUserId(actorUserId, pageable).map(this::toResponse);
    }

    public void deleteDog(Long actorUserId, Long dogId) {
        ensureActorNotBlocked(actorUserId, "DOG_DELETE_FORBIDDEN");

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new ApiException("DOG_FIND_NOT_FOUND", "Dog not found"));

        if (!dog.getUserId().equals(actorUserId)) {
            throw new ApiException("DOG_DELETE_FORBIDDEN", "Only owner can delete dog");
        }

        dogRepository.delete(dog);
    }

    private void ensureActorNotBlocked(Long actorUserId, String forbiddenCode) {
        String status = userStatusLookupRepository.findStatusByUserId(actorUserId);
        if (USER_STATUS_BLOCKED.equals(status)) {
            throw new ApiException(forbiddenCode, "Blocked user cannot perform write actions");
        }
    }

    private DogResponse toResponse(Dog dog) {
        return new DogResponse(
                dog.getId(),
                dog.getUserId(),
                dog.getName(),
                dog.getBreed(),
                dog.getSize(),
                dog.getNeutered(),
                dog.getSociabilityLevel(),
                dog.getReactivityLevel(),
                dog.getNotes(),
                dog.getCreatedAt()
        );
    }
}
