package com.tracker.api.spec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.api.spec.request.SpecCreateRequest;
import com.tracker.common.exception.BusinessException;
import com.tracker.common.exception.ErrorCode;
import com.tracker.spec.domain.SpecHistory;
import com.tracker.spec.service.SpecService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SpecController.class)
@Import(GlobalExceptionHandler.class)
class SpecControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SpecService specService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("스펙 등록 요청 시 201을 반환한다")
    void create_validRequest_returns201() throws Exception {
        SpecCreateRequest request = new SpecCreateRequest("스펙 내용", "최초 등록");
        SpecHistory saved = new SpecHistory(1L, 1, "스펙 내용", "최초 등록");
        given(specService.save(eq(1L), any(), any())).willReturn(saved);

        mockMvc.perform(post("/api/products/1/specs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.version").value(1));
    }

    @Test
    @DisplayName("스펙 목록 조회 시 200을 반환한다")
    void findAll_returns200() throws Exception {
        given(specService.findAll(1L)).willReturn(List.of(
                new SpecHistory(1L, 2, "스펙2", "변경2"),
                new SpecHistory(1L, 1, "스펙1", "변경1")
        ));

        mockMvc.perform(get("/api/products/1/specs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("최신 스펙 조회 시 200을 반환한다")
    void findLatest_returns200() throws Exception {
        given(specService.findLatest(1L)).willReturn(new SpecHistory(1L, 3, "최신 스펙", "최근 변경"));

        mockMvc.perform(get("/api/products/1/specs/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.version").value(3));
    }

    @Test
    @DisplayName("스펙이 없을 때 최신 조회 시 404를 반환한다")
    void findLatest_notFound_returns404() throws Exception {
        given(specService.findLatest(1L)).willThrow(new BusinessException(ErrorCode.SPEC_NOT_FOUND));

        mockMvc.perform(get("/api/products/1/specs/latest"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("특정 버전 스펙 조회 시 200을 반환한다")
    void findByVersion_returns200() throws Exception {
        given(specService.findByVersion(1L, 2)).willReturn(new SpecHistory(1L, 2, "스펙2", "변경2"));

        mockMvc.perform(get("/api/products/1/specs/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.version").value(2));
    }
}