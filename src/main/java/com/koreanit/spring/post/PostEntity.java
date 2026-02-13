package com.koreanit.spring.post;

import java.time.LocalDateTime;

public class PostEntity {
    private Long id;                  // PK
    private Long userId;              // 작성자 ID (FK)
    private String title;             // 제목
    private String content;           // 내용
    private Integer viewCount;        // 조회수
    private Integer commentsCnt;      // 댓글 수
    private LocalDateTime createdAt;  // 작성일
    private LocalDateTime updatedAt;  // 수정일

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Integer getViewCount() { return viewCount; }
    public Integer getCommentsCnt() { return commentsCnt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setViewCount(Integer viewCount) { this.viewCount = viewCount; }
    public void setCommentsCnt(Integer commentsCnt) { this.commentsCnt = commentsCnt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}