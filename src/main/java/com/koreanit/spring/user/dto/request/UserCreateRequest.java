package com.koreanit.spring.user.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserCreateRequest {

    @NotBlank(message = "username은 필수입니다")
    @Size(min = 4, max = 20, message = "username은 4~20자여야 합니다")
    @Pattern(regexp = "^[^\\s]+$", message = "username에는 공백을 포함할 수 없습니다")
    private String username;

    @NotBlank(message = "password는 필수입니다")
    @Size(min = 4, max = 50, message = "password는 4~50자여야 합니다")
    @Pattern(regexp = "^[^\\s]+$", message = "password에는 공백을 포함할 수 없습니다")
    private String password;

    @NotBlank(message = "nickname은 필수입니다")
    @Size(min = 2, max = 20, message = "nickname은 2~20자여야 합니다")
    @Pattern(regexp = "^[^\\s]+$", message = "nickname에는 공백을 포함할 수 없습니다")
    private String nickname;

    @Email(message = "email 형식이 올바르지 않습니다")
    private String email;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getEmail() { return email; }

    public void setEmail(String email) {
        if (email == null) {
            this.email = null;
            return;
        }
        String v = email.trim();
        this.email = v.isEmpty() ? null : v;
    }
}