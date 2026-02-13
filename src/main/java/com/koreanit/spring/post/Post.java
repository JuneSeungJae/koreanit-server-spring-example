package com.koreanit.spring.post;

import java.time.LocalDateTime;

public class Post {

  private final Long id;
  private final Long userId;
  private final String title;
  private final String content;
  private final Integer viewCount;
  private final Integer commentsCnt;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  private Post(
      Long id,
      Long userId,
      String title,
      String content,
      Integer viewCount,
      Integer commentsCnt,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.userId = userId;
    this.title = title;
    this.content = content;
    this.viewCount = viewCount;
    this.commentsCnt = commentsCnt;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Post of(
      Long id,
      Long userId,
      String title,
      String content,
      Integer viewCount,
      Integer commentsCnt,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    if (id == null)
      throw new IllegalArgumentException("id는 필수입니다");
    
    return new Post(id, userId, title, content, viewCount, commentsCnt, createdAt, updatedAt);
  }

  public String summary(int maxLen) {
    if (maxLen <= 0)
      return "";
    if (content == null)
      return "";
    return content.length() <= maxLen ? content : content.substring(0, maxLen) + "...";
  }

  public Long getId() {
    return id;
  }

  public Long getUserId() {
    return userId;
  }

  public String getTitle() {
    return title;
  }

  public String getContent() {
    return content;
  }

  public Integer getViewCount() {
    return viewCount;
  }

  public Integer getCommentsCnt() {
    return commentsCnt;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }
}