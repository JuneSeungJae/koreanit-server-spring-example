package com.koreanit.spring.post;

import com.koreanit.spring.common.error.ApiException;
import com.koreanit.spring.common.error.ErrorCode;
import com.koreanit.spring.security.SecurityUtils;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {

    private static final int MAX_LIMIT = 1000;

    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "limit 값이 유효하지 않습니다");
        }
        return Math.min(limit, MAX_LIMIT);
    }

    private int normalizePage(int page) {
        if (page <= 0) {
            throw new ApiException(ErrorCode.INVALID_REQUEST, "page 값이 유효하지 않습니다");
        }
        return page;
    }

    public Post create(long userId, String title, String content) {
        title = title.trim(); // 제목 앞뒤 공백 제거(정규화)
        long id = postRepository.save(userId, title, content);
        return PostMapper.toDomain(postRepository.findById(id));
    }

    public List<Post> list(int page, int limit) {

        page = normalizePage(page);
        limit = normalizeLimit(limit);

        int offset = (page - 1) * limit;

        return PostMapper.toDomainList(postRepository.findAll(offset, normalizeLimit(limit)));
    }

    @Transactional
    public Post get(long id) {

        int updated = postRepository.increaseViewCount(id);
        if (updated == 0) {
            throw new ApiException(
                    ErrorCode.NOT_FOUND_RESOURCE,
                    "존재하지 않는 게시글입니다. id=" + id);
        }

        return PostMapper.toDomain(postRepository.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or @postService.isOwner(#id)")
    @Transactional
    public Post update(long id, String title, String content) {
        title = title.trim();
        content = content.trim();

        int updated = postRepository.update(id, title, content);
        if (updated == 0) {
            throw new ApiException(ErrorCode.NOT_FOUND_RESOURCE, "존재하지 않는 게시글입니다. id=" + id);
        }

        return PostMapper.toDomain(postRepository.findById(id));
    }

    @PreAuthorize("hasRole('ADMIN') or @postService.isOwner(#id)")
    public void delete(long id) {
        int deleted = postRepository.delete(id);
        if (deleted == 0) {
            throw new ApiException(
                    ErrorCode.NOT_FOUND_RESOURCE,
                    "존재하지 않는 게시글입니다. id=" + id);
        }
    }

    /**
     * 현재 로그인 사용자가 해당 게시글의 작성자인지 확인
     * - PreAuthorize 전용 보조 메서드
     */
    public boolean isOwner(Long postId) {
        Long currentUserId = SecurityUtils.currentUserId();
        if (currentUserId == null) {
            return false;
        }

        return postRepository.isOwner(postId, currentUserId);
    }
}