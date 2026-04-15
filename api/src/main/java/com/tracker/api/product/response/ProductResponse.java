package com.tracker.api.product.response;

import com.tracker.product.domain.Product;
import com.tracker.product.domain.ProductStatus;

public record ProductResponse(
        Long id,
        String name,
        String description,
        String createdBy,
        ProductStatus status
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCreatedBy(),
                product.getStatus()
        );
    }
}
