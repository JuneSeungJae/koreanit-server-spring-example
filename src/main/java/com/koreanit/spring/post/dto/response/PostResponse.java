package com.koreanit.spring.post.dto.response;

import java.time.LocalDateTime;

import com.koreanit.spring.post.Post;

public class PostResponse {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String summary;
    private Integer viewCount;
    private Integer commentsCnt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getSummary() { return summary; }
    public Integer getViewCount() { return viewCount; }
    public Integer getCommentsCnt() { return commentsCnt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // domain -> dto 
    public static PostResponse from(Post p) {
        PostResponse r = new PostResponse();
        r.id = p.getId();
        r.userId = p.getUserId();
        r.title = p.getTitle();
        r.content = p.getContent();
        r.summary = p.summary(5);
        r.viewCount = p.getViewCount();
        r.commentsCnt = p.getCommentsCnt();
        r.createdAt = p.getCreatedAt();
        r.updatedAt = p.getUpdatedAt();
        return r;
    }
}