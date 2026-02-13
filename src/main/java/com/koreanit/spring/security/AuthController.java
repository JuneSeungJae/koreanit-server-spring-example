package com.koreanit.spring.security;

import org.springframework.web.bind.annotation.*;
import com.koreanit.spring.common.dto.ApiResponse;
import com.koreanit.spring.common.error.ApiException;
import com.koreanit.spring.common.error.ErrorCode;
import com.koreanit.spring.user.dto.request.UserLoginRequest;
import com.koreanit.spring.user.dto.response.UserResponse;
import com.koreanit.spring.user.UserMapper;
import com.koreanit.spring.user.UserService;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api")
public class AuthController {

    public static final String SESSION_USER_ID = "LOGIN_USER_ID";

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<Long> login(@RequestBody UserLoginRequest req, HttpSession session) {
      Long userId = userService.login(req.getUsername(), req.getPassword());
      session.setAttribute(SESSION_USER_ID, userId);
      return ApiResponse.ok(userId);
    }

    @PostMapping("/logout")
    public ApiResponse<Void> logout(HttpSession session) {
        session.invalidate();
        return ApiResponse.ok();
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> me(HttpSession session) {
        Object raw = session.getAttribute(SESSION_USER_ID);

        if (!(raw instanceof Long userId)) {
            throw new ApiException(ErrorCode.UNAUTHORIZED, "로그인이 필요합니다");
        }

        return ApiResponse.ok(UserMapper.toResponse(userService.get(userId)));
    }
}