package com.example.walkservice.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.dog.repository.DogRepository;
import com.example.walkservice.user.dto.UserResponse;
import com.example.walkservice.user.entity.User;
import com.example.walkservice.user.entity.UserStatus;
import com.example.walkservice.user.repository.UserRepository;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

class UserServiceTest {

    @Test
    void login_matchesPassword_returnsUser() throws Exception {
        UserRepository repo = mock(UserRepository.class);
        DogRepository dogRepository = mock(DogRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService service = new UserService(repo, dogRepository, passwordEncoder);

        User saved = new User("a@b.com", "hashed-password", UserStatus.ACTIVE, OffsetDateTime.now());
        setId(saved, 10L);
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(saved));
        when(passwordEncoder.matches("1234", "hashed-password")).thenReturn(true);

        UserResponse response = service.login("a@b.com", "1234");
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getEmail()).isEqualTo("a@b.com");
        assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void login_invalidPassword_throwsInvalidCredentials() {
        UserRepository repo = mock(UserRepository.class);
        DogRepository dogRepository = mock(DogRepository.class);
        PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
        UserService service = new UserService(repo, dogRepository, passwordEncoder);

        User saved = new User("a@b.com", "hashed-password", UserStatus.ACTIVE, OffsetDateTime.now());
        when(repo.findByEmail("a@b.com")).thenReturn(Optional.of(saved));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        org.junit.jupiter.api.Assertions.assertThrows(
                ApiException.class,
                () -> service.login("a@b.com", "wrong")
        );
    }

    private static void setId(User user, Long id) throws Exception {
        Field field = User.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(user, id);
    }
}
