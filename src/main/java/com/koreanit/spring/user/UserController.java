package com.koreanit.spring.user;

import java.util.List;
import org.springframework.web.bind.annotation.*;
import com.koreanit.spring.common.dto.ApiResponse;
import com.koreanit.spring.user.dto.request.UserCreateRequest;
import com.koreanit.spring.user.dto.request.UserEmailChangeRequest;
import com.koreanit.spring.user.dto.request.UserNicknameChangeRequest;
import com.koreanit.spring.user.dto.request.UserPasswordChangeRequest;
import com.koreanit.spring.user.dto.response.UserResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ApiResponse<Long> create(@Valid @RequestBody UserCreateRequest req) {
        return ApiResponse.ok(userService.create(req.getUsername(), req.getPassword(), req.getNickname(), req.getEmail()));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserResponse> get(@PathVariable Long id) {
        return ApiResponse.ok(UserMapper.toResponse(userService.get(id)));
    }

    @GetMapping
    public ApiResponse<List<UserResponse>> list(@RequestParam(defaultValue = "1000") int limit) {
        return ApiResponse.ok(UserMapper.toResponseList(userService.list(limit)));
    }

    @PutMapping("/{id}/nickname")
    public ApiResponse<Void> changeNickname(@PathVariable Long id, @RequestBody UserNicknameChangeRequest req) {
        userService.changeNickname(id, req.getNickname());
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/password")
    public ApiResponse<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody UserPasswordChangeRequest req) {
        userService.changePassword(id, req.getPassword());
        return ApiResponse.ok();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ApiResponse.ok();
    }

    @PutMapping("/{id}/email")
    public ApiResponse<Void> changeEmail(
            @PathVariable Long id,
            @Valid @RequestBody UserEmailChangeRequest req) {
        userService.changeEmail(id, req.getEmail());
        return ApiResponse.ok();
    }
}