package com.example.walkservice.dog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.dog.dto.CreateDogRequest;
import com.example.walkservice.dog.dto.UpdateDogRequest;
import com.example.walkservice.dog.entity.Dog;
import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import com.example.walkservice.dog.repository.DogRepository;
import com.example.walkservice.dog.repository.UserStatusLookupRepository;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogRepository dogRepository;

    @Mock
    private UserStatusLookupRepository userStatusLookupRepository;

    @InjectMocks
    private DogService dogService;

    @Test
    void createDog_blockedActor_throwsForbidden() {
        given(userStatusLookupRepository.findStatusByUserId(1L)).willReturn("BLOCKED");

        CreateDogRequest request = new CreateDogRequest(
                "name",
                "breed",
                DogSize.SMALL,
                true,
                DogSociabilityLevel.MEDIUM,
                DogReactivityLevel.LOW,
                null
        );

        ApiException ex = assertThrows(ApiException.class, () -> dogService.createDog(1L, request));
        assertEquals("DOG_CREATE_FORBIDDEN", ex.getCode());
        then(dogRepository).shouldHaveNoInteractions();
    }

    @Test
    void updateDog_notOwner_throwsForbidden() {
        given(userStatusLookupRepository.findStatusByUserId(1L)).willReturn("ACTIVE");

        Dog dog = new Dog(
                2L,
                "name",
                "breed",
                DogSize.MEDIUM,
                false,
                DogSociabilityLevel.HIGH,
                DogReactivityLevel.MEDIUM,
                null,
                OffsetDateTime.now()
        );

        given(dogRepository.findById(10L)).willReturn(Optional.of(dog));

        UpdateDogRequest request = new UpdateDogRequest(
                "newName",
                "newBreed",
                DogSize.LARGE,
                true,
                DogSociabilityLevel.LOW,
                DogReactivityLevel.HIGH,
                "notes"
        );

        ApiException ex = assertThrows(ApiException.class, () -> dogService.updateDog(1L, 10L, request));
        assertEquals("DOG_UPDATE_FORBIDDEN", ex.getCode());
    }
}
