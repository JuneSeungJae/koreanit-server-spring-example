package com.koreanit.spring.comment;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.koreanit.spring.comment.dto.request.CommentCreateRequest;
import com.koreanit.spring.comment.dto.response.CommentResponse;
import com.koreanit.spring.common.dto.ApiResponse;
import com.koreanit.spring.security.SecurityUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CommentController {

  private final CommentService commentService;

  public CommentController(CommentService commentService) {
    this.commentService = commentService;
  }

  @PostMapping("/posts/{postId}/comments")
  public ApiResponse<CommentResponse> create(
      @PathVariable long postId,
      @Valid @RequestBody CommentCreateRequest req) {
    Long userId = SecurityUtils.currentUserId();
    Comment created = commentService.create(postId, userId, req.getContent());
    return ApiResponse.ok(CommentMapper.toResponse(created));
  }

  @GetMapping("/posts/{postId}/comments")
  public ApiResponse<List<CommentResponse>> list(
      @PathVariable long postId,
      @RequestParam(required = false) Long before,
      @RequestParam(defaultValue = "20") int limit) {
    return ApiResponse.ok(
        CommentMapper.toResponseList(commentService.list(postId, before, limit)));
  }

  @DeleteMapping("/comments/{id}")
  public ApiResponse<Void> delete(@PathVariable long id) {
    commentService.delete(id);
    return ApiResponse.ok();
  }
}