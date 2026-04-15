package com.tracker.api.spec.request;

import jakarta.validation.constraints.NotBlank;

public record SpecCreateRequest(

        @NotBlank
        String specContent,

        @NotBlank
        String changeReason
) {}