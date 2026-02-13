package com.koreanit.spring.comment.dto.request;

import jakarta.validation.constraints.NotBlank;

public class CommentCreateRequest {

    @NotBlank(message = "content는 필수입니다")
    private String content;

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}