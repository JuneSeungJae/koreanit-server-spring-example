package com.koreanit.spring.common.error;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST),   // 400
    NOT_FOUND_RESOURCE(HttpStatus.NOT_FOUND),  // 404
    DUPLICATE_RESOURCE(HttpStatus.CONFLICT),   // 409
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),      // 401 
    FORBIDDEN(HttpStatus.FORBIDDEN),            // 403 
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR); // 500
    

    private final HttpStatus status;

    ErrorCode(HttpStatus status) {
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}