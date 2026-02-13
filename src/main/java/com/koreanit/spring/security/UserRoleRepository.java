package com.koreanit.spring.security;

import java.util.List;

public interface UserRoleRepository {

  List<String> findRolesByUserId(Long userId);

  void addRole(Long userId, String role);
}