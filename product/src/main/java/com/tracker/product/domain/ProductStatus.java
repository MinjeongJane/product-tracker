package com.tracker.product.domain;

import java.util.Set;
import java.util.EnumSet;
import java.util.Collections;

public enum ProductStatus {

    IDEA {
        @Override
        public Set<ProductStatus> allowedTransitions() {
            return EnumSet.of(SPEC_DONE);
        }
    },
    SPEC_DONE {
        @Override
        public Set<ProductStatus> allowedTransitions() {
            return EnumSet.of(DEV, IDEA);
        }
    },
    DEV {
        @Override
        public Set<ProductStatus> allowedTransitions() {
            return EnumSet.of(QA, SPEC_DONE);
        }
    },
    QA {
        @Override
        public Set<ProductStatus> allowedTransitions() {
            return EnumSet.of(RELEASED, DEV);
        }
    },
    RELEASED {
        @Override
        public Set<ProductStatus> allowedTransitions() {
            return EnumSet.of(DISCONTINUED);
        }
    },
    DISCONTINUED {
        @Override
        public Set<ProductStatus> allowedTransitions() {
            return Collections.emptySet();
        }
    };

    public abstract Set<ProductStatus> allowedTransitions();

    public boolean canTransitionTo(ProductStatus next) {
        return allowedTransitions().contains(next);
    }
}
