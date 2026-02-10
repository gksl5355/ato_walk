package com.example.walkservice.dog.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.walkservice.common.security.CurrentUser;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.dog.dto.DogResponse;
import com.example.walkservice.dog.entity.DogReactivityLevel;
import com.example.walkservice.dog.entity.DogSize;
import com.example.walkservice.dog.entity.DogSociabilityLevel;
import com.example.walkservice.dog.service.DogService;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DogController.class)
@AutoConfigureMockMvc(addFilters = false)
class DogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DogService dogService;

    @MockBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void get_returnsSuccessEnvelope() throws Exception {
        given(currentUserProvider.currentUser()).willReturn(new CurrentUser(1L));

        DogResponse response = new DogResponse(
                10L,
                1L,
                "name",
                "breed",
                DogSize.MEDIUM,
                false,
                DogSociabilityLevel.HIGH,
                DogReactivityLevel.LOW,
                null,
                OffsetDateTime.parse("2026-02-10T00:00:00Z")
        );
        given(dogService.getDog(1L, 10L)).willReturn(response);

        mockMvc.perform(get("/api/v1/dogs/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(10));
    }

    @Test
    void list_returnsSuccessEnvelopeWithPageContent() throws Exception {
        given(currentUserProvider.currentUser()).willReturn(new CurrentUser(1L));

        DogResponse response = new DogResponse(
                10L,
                1L,
                "name",
                "breed",
                DogSize.MEDIUM,
                false,
                DogSociabilityLevel.HIGH,
                DogReactivityLevel.LOW,
                null,
                OffsetDateTime.parse("2026-02-10T00:00:00Z")
        );

        PageRequest pageable = PageRequest.of(0, 20);
        given(dogService.listDogs(1L, pageable)).willReturn(new PageImpl<>(List.of(response), pageable, 1));

        mockMvc.perform(get("/api/v1/dogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(10));
    }

    @Test
    void delete_returnsSuccessEnvelope() throws Exception {
        given(currentUserProvider.currentUser()).willReturn(new CurrentUser(1L));

        mockMvc.perform(delete("/api/v1/dogs/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        then(dogService).should().deleteDog(1L, 10L);
    }
}
