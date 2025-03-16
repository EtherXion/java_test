package com.sparta.java_test.domain.user.dto;

import lombok.Getter;

@Getter
public class ErrorResponse {
    private final ErrorDetail error;

    public ErrorResponse(String code, String message) {
        this.error = new ErrorDetail(code, message);
    }

    @Getter
    public static class ErrorDetail {
        private final String code;
        private final String message;

        public ErrorDetail() {
            this.code = null;
            this.message = null;
        }

        public ErrorDetail(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}
