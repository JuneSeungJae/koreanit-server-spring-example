package com.koreanit.spring.common.dto;

public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final String code;   // 실패 식별자

    private ApiResponse(boolean success, String message, T data, String code) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public T getData() { return data; }
    public String getCode() { return code; }

    /* ---------- 성공 ---------- */

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, "OK", data, null);
    }

    public static <T> ApiResponse<T> ok(String message, T data) {
        return new ApiResponse<>(true, message, data, null);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>(true, "OK", null, null);
    }

    public static ApiResponse<Void> ok(String message) {
        return new ApiResponse<>(true, message, null, null);
    }

    /* ---------- 실패 ---------- */

    public static ApiResponse<Void> fail(String code, String message) {
        return new ApiResponse<>(false, message, null, code);
    }
}