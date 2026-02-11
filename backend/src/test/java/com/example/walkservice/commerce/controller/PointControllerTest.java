package com.example.walkservice.commerce.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.walkservice.commerce.dto.UserPointsResponse;
import com.example.walkservice.commerce.service.PointService;
import com.example.walkservice.common.security.CurrentUser;
import com.example.walkservice.common.security.CurrentUserProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PointController.class)
@AutoConfigureMockMvc(addFilters = false)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @MockBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void getMyPoints_returnsSuccessEnvelope() throws Exception {
        given(currentUserProvider.currentUser()).willReturn(new CurrentUser(1L));
        given(pointService.getMyPoints(1L)).willReturn(new UserPointsResponse(200000L));

        mockMvc.perform(get("/api/v1/users/me/points"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.balance").value(200000));
    }
}
