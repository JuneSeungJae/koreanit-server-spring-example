package com.koreanit.spring.user;

import java.time.LocalDateTime;

public class User {

  private final Long id;
  private final String username;
  private final String email;
  private final String password; // Domain에는 있을 수 있다(외부 응답은 DTO가 막는다)
  private final String nickname;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  private User(
      Long id,
      String username,
      String email,
      String password,
      String nickname,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.username = username;
    this.email = email;
    this.password = password;
    this.nickname = nickname;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  // Domain 생성 규칙(검증)을 모으는 팩토리
  public static User of(
      Long id,
      String username,
      String email,
      String password,
      String nickname,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    if (id == null)
      throw new IllegalArgumentException("id는 필수입니다");
    if (username == null)
      throw new IllegalArgumentException("username은 필수입니다");

    return new User(id, username, email, password, nickname, createdAt, updatedAt);
  }

  public Long getId() {
    return id;
  }

  public String getUsername() {
    return username;
  }
  
  public String displayName() {
  if (nickname != null && !nickname.isBlank()) {
    return nickname + "(" + username + ")";
  }else{
    return username;
  }
}

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public String getNickname() {
    return nickname;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}