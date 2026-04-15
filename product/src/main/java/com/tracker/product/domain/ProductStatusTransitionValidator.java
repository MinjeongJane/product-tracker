package com.tracker.product.domain;

import com.tracker.common.exception.BusinessException;
import com.tracker.common.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class ProductStatusTransitionValidator {

    public void validate(ProductStatus current, ProductStatus next) {
        if (!current.canTransitionTo(next)) {
            throw new BusinessException(
                ErrorCode.INVALID_STATUS_TRANSITION,
                current.name() + "에서 " + next.name() + "으로 전이할 수 없습니다."
            );
        }
    }
}
