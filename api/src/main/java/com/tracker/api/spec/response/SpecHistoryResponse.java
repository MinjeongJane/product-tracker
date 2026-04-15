package com.tracker.api.spec.response;

import com.tracker.spec.domain.SpecHistory;

public record SpecHistoryResponse(
        Long id,
        Long productId,
        int version,
        String specContent,
        String changeReason
) {
    public static SpecHistoryResponse from(SpecHistory specHistory) {
        return new SpecHistoryResponse(
                specHistory.getId(),
                specHistory.getProductId(),
                specHistory.getVersion(),
                specHistory.getSpecContent(),
                specHistory.getChangeReason()
        );
    }
}