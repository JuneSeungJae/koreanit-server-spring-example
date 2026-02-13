package com.koreanit.spring.post;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class JdbcPostRepository implements PostRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcPostRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<PostEntity> rowMapper = (rs, rowNum) -> {
    PostEntity e = new PostEntity();
    e.setId(rs.getLong("id"));
    e.setUserId(rs.getLong("user_id"));
    e.setTitle(rs.getString("title"));
    e.setContent(rs.getString("content"));
    e.setViewCount(rs.getInt("view_count"));
    e.setCommentsCnt(rs.getInt("comments_cnt"));
    e.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    if (rs.getTimestamp("updated_at") != null) {
      e.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
    }
    return e;
  };

  @Override
  public long save(long userId, String title, String content) {
    String sql = "INSERT INTO posts(user_id, title, content) VALUES (?, ?, ?)";
    KeyHolder keyHolder = new GeneratedKeyHolder();

    jdbcTemplate.update(con -> {
      PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setLong(1, userId);
      ps.setString(2, title);
      ps.setString(3, content);
      return ps;
    }, keyHolder);

    return keyHolder.getKey().longValue();
  }

  @Override
  public List<PostEntity> findAll(int offset, int limit) {
    String sql = """
        SELECT id, user_id, title, content, view_count, comments_cnt, created_at, updated_at
        FROM posts
        ORDER BY id DESC
        LIMIT ? OFFSET ?
        """;
    return jdbcTemplate.query(sql, rowMapper, limit, offset);
  }

  @Override
  public long countAll() {
    String sql = "SELECT COUNT(*) FROM posts";
    Long cnt = jdbcTemplate.queryForObject(sql, Long.class);
    return cnt == null ? 0 : cnt;
  }

  @Override
  public PostEntity findById(long id) {
    String sql = """
        SELECT id, user_id, title, content, view_count, comments_cnt, created_at, updated_at
        FROM posts
        WHERE id = ?
        """;
    return jdbcTemplate.queryForObject(sql, rowMapper, id);
  }

  @Override
  public int update(long id, String title, String content) {
    String sql = "UPDATE posts SET title = ?, content = ? WHERE id = ?";
    return jdbcTemplate.update(sql, title, content, id);
  }

  @Override
  public int delete(long id) {
    String sql = "DELETE FROM posts WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  @Override
  public int increaseViewCount(long id) {
    String sql = """
            UPDATE posts
            SET view_count = view_count + 1
            WHERE id = ?
        """;
    return jdbcTemplate.update(sql, id);
  }

  @Override
  public boolean isOwner(long postId, long userId) {
      String sql = """
          SELECT COUNT(*)
          FROM posts
          WHERE id = ? AND user_id = ?
      """;

      int count = jdbcTemplate.queryForObject(
          sql,
          Integer.class,
          postId,
          userId
      );

      return count > 0;
  }

  @Override
  public int increaseCommentsCnt(long postId) {
    String sql = """
            UPDATE posts
            SET comments_cnt = comments_cnt + 1
            WHERE id = ?
        """;
    return jdbcTemplate.update(sql, postId);
  }

  @Override
  public int decreaseCommentsCnt(long postId) {
    String sql = """
            UPDATE posts
            SET comments_cnt = CASE
                WHEN comments_cnt > 0 THEN comments_cnt - 1
                ELSE 0
            END
            WHERE id = ?
        """;
    return jdbcTemplate.update(sql, postId);
  }

  @Override
  public boolean existsById(long postId) {
    String sql = "SELECT COUNT(*) FROM posts WHERE id = ?";
    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, postId);
    return count != null && count > 0;
  }
}