package com.example.walkservice.commerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.walkservice.commerce.dto.OrderItemResponse;
import com.example.walkservice.commerce.dto.OrderResponse;
import com.example.walkservice.commerce.entity.OrderStatus;
import com.example.walkservice.commerce.entity.PaymentMethod;
import com.example.walkservice.commerce.service.OrderService;
import com.example.walkservice.common.exception.ApiException;
import com.example.walkservice.common.exception.GlobalExceptionHandler;
import com.example.walkservice.common.security.CurrentUser;
import com.example.walkservice.common.security.CurrentUserProvider;
import java.time.OffsetDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private CurrentUserProvider currentUserProvider;

    @Test
    void createOrder_successEnvelope() throws Exception {
        given(currentUserProvider.currentUser()).willReturn(new CurrentUser(1L));

        OrderResponse response = new OrderResponse(
                10L,
                OrderStatus.CREATED,
                32000L,
                OffsetDateTime.parse("2026-02-11T00:00:00Z"),
                List.of(new OrderItemResponse(1L, 2L, 1, 32000L, 32000L))
        );
        given(orderService.createOrder(1L, PaymentMethod.POINT)).willReturn(response);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"paymentMethod\":\"POINT\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CREATED"));
    }

    @Test
    void createOrder_insufficientPoints_returnsErrorEnvelope() throws Exception {
        given(currentUserProvider.currentUser()).willReturn(new CurrentUser(1L));
        given(orderService.createOrder(eq(1L), any()))
                .willThrow(new ApiException("ORDER_CREATE_POINT_INSUFFICIENT", "Insufficient point balance"));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("ORDER_CREATE_POINT_INSUFFICIENT"));
    }
}
