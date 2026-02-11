package com.example.walkservice.commerce.controller;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.walkservice.commerce.dto.PageMetaResponse;
import com.example.walkservice.commerce.dto.ProductPageDataResponse;
import com.example.walkservice.commerce.dto.ProductSummaryResponse;
import com.example.walkservice.commerce.entity.ProductCategory;
import com.example.walkservice.commerce.entity.ProductStatus;
import com.example.walkservice.commerce.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    void list_returnsSuccessEnvelope() throws Exception {
        ProductSummaryResponse summary = new ProductSummaryResponse(
                1L,
                "튼튼 사료 2kg",
                ProductCategory.FEED,
                32000L,
                10,
                ProductStatus.ON_SALE
        );
        ProductPageDataResponse response = new ProductPageDataResponse(
                List.of(summary),
                new PageMetaResponse(0, 20, 1, 1)
        );

        given(productService.listProducts(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .willReturn(response);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }
}
