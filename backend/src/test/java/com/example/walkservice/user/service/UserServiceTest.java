package com.example.walkservice.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.user.dto.UserResponse;
import com.example.walkservice.user.entity.User;
import com.example.walkservice.user.entity.UserStatus;
import com.example.walkservice.user.repository.UserRepository;
import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @Test
    void login_createsUserIfMissing() throws Exception {
        UserRepository repo = mock(UserRepository.class);
        UserService service = new UserService(repo);

        when(repo.findByEmail("a@b.com")).thenReturn(Optional.empty());

        User saved = new User("a@b.com", UserStatus.ACTIVE, OffsetDateTime.now());
        setId(saved, 10L);
        when(repo.save(org.mockito.ArgumentMatchers.any(User.class))).thenReturn(saved);

        UserResponse response = service.login("a@b.com");
        assertThat(response.getId()).isEqualTo(10L);
        assertThat(response.getEmail()).isEqualTo("a@b.com");
        assertThat(response.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    private static void setId(User user, Long id) throws Exception {
        Field field = User.class.getDeclaredField("id");
        field.setAccessible(true);
        field.set(user, id);
    }
}
