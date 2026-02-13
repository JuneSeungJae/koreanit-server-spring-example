package com.koreanit.spring.user.dto.request;

import jakarta.validation.constraints.Email;

public class UserEmailChangeRequest {

    @Email(message = "email 형식이 올바르지 않습니다")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null) {
            this.email = null;
            return;
        }
        String v = email.trim();
        this.email = v.isEmpty() ? null : v;
    }
}