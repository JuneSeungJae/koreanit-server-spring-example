// UserRepository의 JdbcTemplate 기반 구현체
// SQL 실행과 ResultSet → Entity 매핑만 담당한다.
package com.koreanit.spring.user;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcUserRepository implements UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public JdbcUserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<UserEntity> userRowMapper = (rs, rowNum) -> {
        UserEntity u = new UserEntity();
        u.setId(rs.getLong("id"));
        u.setUsername(rs.getString("username"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setNickname(rs.getString("nickname"));

        Timestamp c = rs.getTimestamp("created_at");
        if (c != null)
            u.setCreatedAt(c.toLocalDateTime());

        Timestamp up = rs.getTimestamp("updated_at");
        if (up != null)
            u.setUpdatedAt(up.toLocalDateTime());

        return u;
    };

    @Override
    public Long save(String username, String passwordHash, String nickname, String email) {
        String sql = """
                INSERT INTO users (username, password, nickname, email)
                VALUES (?, ?, ?, ?)
                """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, nickname);
            ps.setString(4, email);
            return ps;
        }, keyHolder);

        Number key = keyHolder.getKey();
        return (key != null) ? key.longValue() : null;
    }

    @Override
    public UserEntity findById(Long id) {
        String sql = """
                SELECT id, username, email, password, nickname, created_at, updated_at
                FROM users
                WHERE id = ?
                """;
        return jdbcTemplate.queryForObject(sql, userRowMapper, id);
    }

    @Override
    public UserEntity findByUsername(String username) {
        String sql = """
                SELECT id, username, email, password, nickname, created_at, updated_at
                FROM users
                WHERE username = ?
                """;
        return jdbcTemplate.queryForObject(sql, userRowMapper, username);
    }

    @Override
    public List<UserEntity> findAll(int limit) {
        String sql = """
                SELECT id, username, email, password, nickname, created_at, updated_at
                FROM users
                ORDER BY id DESC
                LIMIT ?
                """;
        return jdbcTemplate.query(sql, userRowMapper, limit);
    }

    @Override
    public int updateNickname(Long id, String nickname) {
        String sql = """
                UPDATE users
                SET nickname = ?, updated_at = NOW()
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql, nickname, id);
    }

    @Override
    public int updatePassword(Long id, String passwordHash) {
        String sql = """
                UPDATE users
                SET password = ?, updated_at = NOW()
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql, passwordHash, id);
    }

    @Override
    public int deleteById(Long id) {
        String sql = """
                DELETE FROM users
                WHERE id = ?
                """;
        return jdbcTemplate.update(sql, id);
    }

    @Override
    public int updateEmail(Long id, String email) {
        return jdbcTemplate.update(
                "update users set email = ? where id = ?",
                email, id);
    }
}