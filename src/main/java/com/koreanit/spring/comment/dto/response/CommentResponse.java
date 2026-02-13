package com.koreanit.spring.comment.dto.response;

import java.time.LocalDateTime;

public class CommentResponse {

    private final long id;
    private final long postId;
    private final long userId;
    private final String content;
    private final LocalDateTime createdAt;

    public CommentResponse(long id, long postId, long userId, String content, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.content = content;
        this.createdAt = createdAt;
    }

    public long getId() { return id; }
    public long getPostId() { return postId; }
    public long getUserId() { return userId; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}