package com.koreanit.spring.user;

import java.util.List;

public interface UserRepository {

    Long save(String username, String passwordHash, String nickname, String email);

    UserEntity findById(Long id);

    UserEntity findByUsername(String username);

    List<UserEntity> findAll(int limit);

    int updateNickname(Long id, String nickname);

    int updatePassword(Long id, String passwordHash);

    int deleteById(Long id);

    int updateEmail(Long id, String email);
}