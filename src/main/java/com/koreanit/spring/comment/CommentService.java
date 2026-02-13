package com.koreanit.spring.comment;

import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.koreanit.spring.common.error.ApiException;
import com.koreanit.spring.common.error.ErrorCode;
import com.koreanit.spring.post.PostRepository;
import com.koreanit.spring.security.SecurityUtils;

@Service
public class CommentService {

  private static final int MAX_LIMIT = 1000;

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;

  public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
    this.commentRepository = commentRepository;
    this.postRepository = postRepository;
  }

  private int normalizeLimit(int limit) {
    if (limit <= 0) {
      throw new ApiException(ErrorCode.INVALID_REQUEST, "limit 값이 유효하지 않습니다");
    }
    return Math.min(limit, MAX_LIMIT);
  }

  @Transactional
  public Comment create(long postId, long userId, String content) {
    if (!postRepository.existsById(postId)) {
      throw new ApiException(ErrorCode.NOT_FOUND_RESOURCE, "게시글이 존재하지 않습니다. id=" + postId);
    }

    long id = commentRepository.save(postId, userId, content);
    postRepository.increaseCommentsCnt(postId);

    return CommentMapper.toDomain(commentRepository.findById(id));
  }

  public List<Comment> list(long postId, Long before, int limit) {
    return CommentMapper.toDomainList(
        commentRepository.findAllByPostId(postId, before, normalizeLimit(limit)));
  }

  public boolean isOwner(long id) {
    Long userId = SecurityUtils.currentUserId();
    if (userId == null) {
      return false;
    }
    return commentRepository.isOwner(id, userId);
  }

  @PreAuthorize("hasRole('ADMIN') or @commentService.isOwner(#id)")
  @Transactional
  public void delete(long id) {

    try {
      CommentEntity comment = commentRepository.findById(id);
      int deleted = commentRepository.deleteById(id);
      if (deleted == 0) {
        throw new ApiException(
            ErrorCode.NOT_FOUND_RESOURCE,
            "존재하지 않는 댓글입니다. id=" + id);
      }
      Long postId = comment.getPostId();
      if (postId != null)
        postRepository.decreaseCommentsCnt(postId);

    } catch (EmptyResultDataAccessException e) {
      throw new ApiException(ErrorCode.NOT_FOUND_RESOURCE, "댓글이 존재하지 않습니다");
    }

  }

}