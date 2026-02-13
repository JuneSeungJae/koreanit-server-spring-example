package com.koreanit.spring.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class PostUpdateRequest {
    @NotBlank(message = "title은 필수입니다")
    @Size(max = 200, message = "title은 200자 이하여야 합니다")
    private String title;
    
    @NotBlank(message = "content는 필수입니다")
    private String content;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}