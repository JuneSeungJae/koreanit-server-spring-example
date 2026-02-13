package com.koreanit.spring.security;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserRoleRepository implements UserRoleRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcUserRoleRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public List<String> findRolesByUserId(Long userId) {
    String sql = """
        SELECT role
        FROM user_roles
        WHERE user_id = ?
        ORDER BY role
        """;

    return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("role"), userId);
  }

  @Override
  public void addRole(Long userId, String role) {
    String sql = """
        INSERT INTO user_roles (user_id, role)
        VALUES (?, ?)
        """;

    jdbcTemplate.update(sql, userId, role);
  }
}