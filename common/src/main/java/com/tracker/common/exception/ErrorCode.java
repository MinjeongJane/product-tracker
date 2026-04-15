package com.tracker.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_STATUS_TRANSITION(HttpStatus.BAD_REQUEST, "허용되지 않은 상태 전이입니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    SPEC_NOT_FOUND(HttpStatus.NOT_FOUND, "스펙 이력을 찾을 수 없습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    PRODUCT_DELETE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "IDEA 상태의 상품만 삭제할 수 있습니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}