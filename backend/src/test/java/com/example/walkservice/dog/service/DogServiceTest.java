package com.example.walkservice.dog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.security.BlockedWriteGuard;
import com.example.walkservice.dog.dto.CreateDogRequest;
import com.example.walkservice.dog.dto.UpdateDogRequest;
import com.example.walkservice.dog.entity.Dog;
import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import com.example.walkservice.dog.repository.DogRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class DogServiceTest {

    @Mock
    private DogRepository dogRepository;

    @Mock
    private BlockedWriteGuard blockedWriteGuard;

    @InjectMocks
    private DogService dogService;

    @Test
    void createDog_blockedActor_throwsForbidden() {
        willThrow(new ApiException("DOG_CREATE_FORBIDDEN", "Blocked user cannot perform write actions"))
                .given(blockedWriteGuard)
                .ensureNotBlocked(1L, "DOG_CREATE_FORBIDDEN");

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

    @Test
    void getDog_notOwner_throwsForbidden() {
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

        ApiException ex = assertThrows(ApiException.class, () -> dogService.getDog(1L, 10L));
        assertEquals("DOG_GET_FORBIDDEN", ex.getCode());
    }

    @Test
    void deleteDog_blockedActor_throwsForbidden() {
        willThrow(new ApiException("DOG_DELETE_FORBIDDEN", "Blocked user cannot perform write actions"))
                .given(blockedWriteGuard)
                .ensureNotBlocked(1L, "DOG_DELETE_FORBIDDEN");

        ApiException ex = assertThrows(ApiException.class, () -> dogService.deleteDog(1L, 10L));
        assertEquals("DOG_DELETE_FORBIDDEN", ex.getCode());
        then(dogRepository).shouldHaveNoInteractions();
    }

    @Test
    void deleteDog_notOwner_throwsForbidden() {
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

        ApiException ex = assertThrows(ApiException.class, () -> dogService.deleteDog(1L, 10L));
        assertEquals("DOG_DELETE_FORBIDDEN", ex.getCode());
    }

    @Test
    void listDogs_returnsPage() {
        PageRequest pageable = PageRequest.of(0, 20);

        Dog dog = new Dog(
                1L,
                "name",
                "breed",
                DogSize.MEDIUM,
                false,
                DogSociabilityLevel.HIGH,
                DogReactivityLevel.MEDIUM,
                null,
                OffsetDateTime.now()
        );

        given(dogRepository.findAllByUserId(1L, pageable)).willReturn(new PageImpl<>(List.of(dog), pageable, 1));

        assertEquals(1, dogService.listDogs(1L, pageable).getTotalElements());
        then(dogRepository).should().findAllByUserId(1L, pageable);
    }
}
