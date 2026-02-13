package com.koreanit.spring.user;

import java.time.LocalDateTime;

public class UserEntity {
    private Long id;                 // PK
    private String username;         // 로그인 아이디
    private String email;            // 이메일
    private String password;         // 비밀번호 해시
    private String nickname;         // 닉네임
    private LocalDateTime createdAt; // 가입일
    private LocalDateTime updatedAt; // 수정일

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getNickname() { return nickname; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}