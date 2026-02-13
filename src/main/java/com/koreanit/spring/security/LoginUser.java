package com.koreanit.spring.security;

public class LoginUser {

  private final Long id;
  private final String username;
  private final String nickname;

  public LoginUser(Long id, String username, String nickname) {
    this.id = id;
    this.username = username;
    this.nickname = nickname;
  }

  public Long getId() { return id; }
  public String getUsername() { return username; }
  public String getNickname() { return nickname; }
}