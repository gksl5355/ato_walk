package com.example.walkservice.dog.service;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.dog.dto.CreateDogRequest;
import com.example.walkservice.dog.dto.DogResponse;
import com.example.walkservice.dog.dto.UpdateDogRequest;
import com.example.walkservice.dog.entity.Dog;
import com.example.walkservice.dog.repository.DogRepository;
import java.time.OffsetDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DogService {

    private final DogRepository dogRepository;
    private final BlockedWriteGuard blockedWriteGuard;

    public DogService(DogRepository dogRepository, BlockedWriteGuard blockedWriteGuard) {
        this.dogRepository = dogRepository;
        this.blockedWriteGuard = blockedWriteGuard;
    }

    public DogResponse createDog(Long actorUserId, CreateDogRequest request) {
        blockedWriteGuard.ensureNotBlocked(actorUserId, "DOG_CREATE_FORBIDDEN");

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
        blockedWriteGuard.ensureNotBlocked(actorUserId, "DOG_UPDATE_FORBIDDEN");

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
        blockedWriteGuard.ensureNotBlocked(actorUserId, "DOG_DELETE_FORBIDDEN");

        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new ApiException("DOG_FIND_NOT_FOUND", "Dog not found"));

        if (!dog.getUserId().equals(actorUserId)) {
            throw new ApiException("DOG_DELETE_FORBIDDEN", "Only owner can delete dog");
        }

        dogRepository.delete(dog);
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
