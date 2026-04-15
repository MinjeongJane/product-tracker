package com.tracker.product.domain;

import com.tracker.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductStatusTransitionValidatorTest {

    private ProductStatusTransitionValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ProductStatusTransitionValidator();
    }

    @ParameterizedTest
    @DisplayName("허용된 상태 전이는 예외 없이 통과한다")
    @CsvSource({
            "IDEA, SPEC_DONE",
            "SPEC_DONE, DEV",
            "SPEC_DONE, IDEA",
            "DEV, QA",
            "DEV, SPEC_DONE",
            "QA, RELEASED",
            "QA, DEV",
            "RELEASED, DISCONTINUED"
    })
    void validate_allowedTransition_passes(ProductStatus from, ProductStatus to) {
        assertThatNoException().isThrownBy(() -> validator.validate(from, to));
    }

    @ParameterizedTest
    @DisplayName("허용되지 않은 상태 전이는 BusinessException을 던진다")
    @CsvSource({
            "IDEA, DEV",
            "IDEA, QA",
            "IDEA, RELEASED",
            "IDEA, DISCONTINUED",
            "DEV, IDEA",
            "RELEASED, IDEA",
            "DISCONTINUED, IDEA"
    })
    void validate_invalidTransition_throwsBusinessException(ProductStatus from, ProductStatus to) {
        assertThatThrownBy(() -> validator.validate(from, to))
                .isInstanceOf(BusinessException.class);
    }
}
