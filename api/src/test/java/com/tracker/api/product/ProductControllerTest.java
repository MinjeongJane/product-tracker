package com.tracker.api.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.api.product.request.ProductCreateRequest;
import com.tracker.api.product.request.ProductStatusUpdateRequest;
import com.tracker.common.exception.BusinessException;
import com.tracker.common.exception.ErrorCode;
import com.tracker.product.domain.Product;
import com.tracker.product.domain.ProductStatus;
import com.tracker.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.tracker.common.exception.GlobalExceptionHandler;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("상품 등록 요청 시 201을 반환한다")
    void create_validRequest_returns201() throws Exception {
        ProductCreateRequest request = new ProductCreateRequest("상품명", "설명", "hong");
        Product product = new Product("상품명", "설명", "hong");
        given(productService.create(any())).willReturn(product);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("상품 목록 조회 시 200을 반환한다")
    void findAll_returns200() throws Exception {
        given(productService.findAll()).willReturn(List.of(new Product("상품명", "설명", "hong")));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 404를 반환한다")
    void findById_notFound_returns404() throws Exception {
        given(productService.findById(999L)).willThrow(new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("상태 변경 요청 시 200을 반환한다")
    void updateStatus_validRequest_returns200() throws Exception {
        ProductStatusUpdateRequest request = new ProductStatusUpdateRequest(ProductStatus.SPEC_DONE);
        Product product = new Product("상품명", "설명", "hong");
        product.changeStatus(ProductStatus.SPEC_DONE);
        given(productService.updateStatus(eq(1L), any())).willReturn(product);

        mockMvc.perform(patch("/api/products/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @DisplayName("허용되지 않은 상태 전이 시 400을 반환한다")
    void updateStatus_invalidTransition_returns400() throws Exception {
        ProductStatusUpdateRequest request = new ProductStatusUpdateRequest(ProductStatus.DEV);
        given(productService.updateStatus(eq(1L), any()))
                .willThrow(new BusinessException(ErrorCode.INVALID_STATUS_TRANSITION));

        mockMvc.perform(patch("/api/products/1/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("상품 삭제 요청 시 204를 반환한다")
    void delete_validRequest_returns204() throws Exception {
        willDoNothing().given(productService).delete(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("IDEA 상태가 아닌 상품 삭제 시 400을 반환한다")
    void delete_nonIdeaStatus_returns400() throws Exception {
        willThrow(new BusinessException(ErrorCode.PRODUCT_DELETE_NOT_ALLOWED)).given(productService).delete(1L);

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}