package com.tracker.api.product.request;

import com.tracker.product.domain.ProductStatus;
import jakarta.validation.constraints.NotNull;

public record ProductStatusUpdateRequest(

        @NotNull
        ProductStatus status
) {}
