package com.tracker.api.product;

import com.tracker.api.product.request.ProductCreateRequest;
import com.tracker.api.product.request.ProductStatusUpdateRequest;
import com.tracker.api.product.response.ProductResponse;
import com.tracker.common.response.ApiResponse;
import com.tracker.product.domain.Product;
import com.tracker.product.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> create(@RequestBody @Valid ProductCreateRequest request) {
        Product product = new Product(request.name(), request.description(), request.createdBy());
        return ApiResponse.success(ProductResponse.from(productService.create(product)));
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> findAll() {
        List<ProductResponse> products = productService.findAll().stream()
                .map(ProductResponse::from)
                .toList();
        return ApiResponse.success(products);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductResponse> findById(@PathVariable Long id) {
        return ApiResponse.success(ProductResponse.from(productService.findById(id)));
    }

    @PatchMapping("/{id}/status")
    public ApiResponse<ProductResponse> updateStatus(
            @PathVariable Long id,
            @RequestBody @Valid ProductStatusUpdateRequest request) {
        return ApiResponse.success(ProductResponse.from(productService.updateStatus(id, request.status())));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        productService.delete(id);
    }
}
