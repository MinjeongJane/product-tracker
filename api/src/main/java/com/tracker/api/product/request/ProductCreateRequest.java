package com.tracker.api.product.request;

import jakarta.validation.constraints.NotBlank;

public record ProductCreateRequest(

        @NotBlank
        String name,

        String description,

        @NotBlank
        String createdBy
) {}
