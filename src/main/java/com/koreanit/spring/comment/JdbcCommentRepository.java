package com.koreanit.spring.comment;

import java.sql.Statement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcCommentRepository implements CommentRepository {

  private final JdbcTemplate jdbcTemplate;

  public JdbcCommentRepository(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  private final RowMapper<CommentEntity> rowMapper = (rs, rowNum) -> {
    CommentEntity e = new CommentEntity();
    e.setId(rs.getLong("id"));
    e.setPostId(rs.getLong("post_id"));
    e.setUserId(rs.getLong("user_id"));
    e.setContent(rs.getString("content"));
    e.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
    return e;
  };

  @Override
  public long save(long postId, long userId, String content) {
    String sql = """
            INSERT INTO comments (post_id, user_id, content, created_at)
            VALUES (?, ?, ?, NOW())
        """;

    KeyHolder keyHolder = new GeneratedKeyHolder();
    jdbcTemplate.update(con -> {
      var ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
      ps.setLong(1, postId);
      ps.setLong(2, userId);
      ps.setString(3, content);
      return ps;
    }, keyHolder);

    return keyHolder.getKey().longValue();
  }

  @Override
  public CommentEntity findById(long id) {
    String sql = """
            SELECT id, post_id, user_id, content, created_at
            FROM comments
            WHERE id = ?
        """;
    return jdbcTemplate.queryForObject(sql, rowMapper, id);
  }

  @Override
  public List<CommentEntity> findAllByPostId(long postId, Long beforeId, int limit) {

    String sql;
    Object[] args;

    if (beforeId == null) {
      sql = """
              SELECT id, post_id, user_id, content, created_at
              FROM comments
              WHERE post_id = ?
              ORDER BY id DESC
              LIMIT ?
          """;
      args = new Object[] { postId, limit };
    } else {
      sql = """
              SELECT id, post_id, user_id, content, created_at
              FROM comments
              WHERE post_id = ?
                AND id < ?
              ORDER BY id DESC
              LIMIT ?
          """;
      args = new Object[] { postId, beforeId, limit };
    }

    return jdbcTemplate.query(sql, rowMapper, args);
  }

  @Override
  public int deleteById(long id) {
    String sql = "DELETE FROM comments WHERE id = ?";
    return jdbcTemplate.update(sql, id);
  }

  @Override
  public boolean isOwner(long commentId, long userId) {
      String sql = """
          SELECT COUNT(*)
          FROM comments
          WHERE id = ? AND user_id = ?
      """;

      int count = jdbcTemplate.queryForObject(
          sql,
          Integer.class,
          commentId,
          userId
      );

      return count > 0;
  }
}