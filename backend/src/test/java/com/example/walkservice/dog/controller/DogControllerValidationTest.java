package com.example.walkservice.dog.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.walkservice.common.exception.GlobalExceptionHandler;
import com.example.walkservice.common.security.CurrentUser;
import com.example.walkservice.common.security.CurrentUserProvider;
import com.example.walkservice.dog.service.DogService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.BDDMockito.given;

@WebMvcTest(controllers = DogController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DogControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DogService dogService;

    @MockBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void create_validationFailure_returnsCommonEnvelope() throws Exception {
        given(currentUserProvider.currentUser()).willReturn(new CurrentUser(1L));

        mockMvc.perform(post("/api/v1/dogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("COMMON_VALIDATION_FAILED"));
    }
}
