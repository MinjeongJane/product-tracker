package com.tracker.api.spec;

import com.tracker.api.spec.request.SpecCreateRequest;
import com.tracker.api.spec.response.SpecHistoryResponse;
import com.tracker.common.response.ApiResponse;
import com.tracker.spec.service.SpecService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/specs")
public class SpecController {

    private final SpecService specService;

    public SpecController(SpecService specService) {
        this.specService = specService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<SpecHistoryResponse> create(
            @PathVariable Long productId,
            @RequestBody @Valid SpecCreateRequest request) {
        return ApiResponse.success(SpecHistoryResponse.from(
                specService.save(productId, request.specContent(), request.changeReason())));
    }

    @GetMapping
    public ApiResponse<List<SpecHistoryResponse>> findAll(@PathVariable Long productId) {
        List<SpecHistoryResponse> specs = specService.findAll(productId).stream()
                .map(SpecHistoryResponse::from)
                .toList();
        return ApiResponse.success(specs);
    }

    @GetMapping("/latest")
    public ApiResponse<SpecHistoryResponse> findLatest(@PathVariable Long productId) {
        return ApiResponse.success(SpecHistoryResponse.from(specService.findLatest(productId)));
    }

    @GetMapping("/{version}")
    public ApiResponse<SpecHistoryResponse> findByVersion(
            @PathVariable Long productId,
            @PathVariable int version) {
        return ApiResponse.success(SpecHistoryResponse.from(specService.findByVersion(productId, version)));
    }
}