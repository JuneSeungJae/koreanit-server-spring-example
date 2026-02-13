package com.koreanit.spring.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserPasswordChangeRequest {

    @NotBlank(message = "password는 필수입니다")
    @Size(min = 4, max = 50, message = "password는 4~50자여야 합니다")
    @Pattern(regexp = "^[^\\s]+$", message = "password에는 공백을 포함할 수 없습니다")
    private String password;

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}