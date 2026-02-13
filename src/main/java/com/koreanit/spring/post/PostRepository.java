package com.koreanit.spring.post;

import java.util.List;

public interface PostRepository {
    long save(long userId, String title, String content);

    List<PostEntity> findAll(int offset, int limit);

    PostEntity findById(long id);

    int update(long id, String title, String content);

    int delete(long id);

    int increaseViewCount(long id);

    boolean isOwner(long postId, long userId);

    int increaseCommentsCnt(long postId);

    int decreaseCommentsCnt(long postId);

    long countAll();

    boolean existsById(long postId);

}