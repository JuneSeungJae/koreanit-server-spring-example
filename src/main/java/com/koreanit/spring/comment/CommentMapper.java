package com.koreanit.spring.comment;

import java.util.ArrayList;
import java.util.List;

import com.koreanit.spring.comment.dto.response.CommentResponse;

public class CommentMapper {

    private CommentMapper() {}

    public static Comment toDomain(CommentEntity e) {
        return Comment.of(
            e.getId(),
            e.getPostId(),
            e.getUserId(),
            e.getContent(),
            e.getCreatedAt()
        );
    }

    public static List<Comment> toDomainList(List<CommentEntity> entities) {
        List<Comment> result = new ArrayList<>(entities.size());
        for (CommentEntity e : entities) {
            result.add(toDomain(e));
        }
        return result;
    }

    public static CommentResponse toResponse(Comment c) {
        return new CommentResponse(
            c.getId(),
            c.getPostId(),
            c.getUserId(),
            c.getContent(),
            c.getCreatedAt()
        );
    }

    public static List<CommentResponse> toResponseList(List<Comment> list) {
        List<CommentResponse> result = new ArrayList<>(list.size());
        for (Comment c : list) {
            result.add(toResponse(c));
        }
        return result;
    }
}