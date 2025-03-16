package com.sparta.java_test.domain.user.controller;

import com.sparta.java_test.domain.user.dto.*;
import com.sparta.java_test.domain.user.entity.AuthUser;
import com.sparta.java_test.domain.user.entity.User;
import com.sparta.java_test.domain.user.entity.UserRole;
import com.sparta.java_test.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> join(@RequestBody SignupRequest signupRequest) {
        try {
            SignupResponse signupResponse = authService.signup(signupRequest);
            return ResponseEntity.ok(signupResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("USER_ALREADY_EXISTS", "이미 가입된 사용자입니다."));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody SignRequest signRequest) {
        try {
            SignResponse signResponse = authService.sign(signRequest);
            return ResponseEntity.ok(signResponse);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("INVALID_CREDENTIALS", "아이디 또는 비밀번호가 올바르지 않습니다."));
        }
    }

    @PatchMapping("/admin/users/{userId}/roles")
    public ResponseEntity<?> grantAdminRole(@PathVariable Long userId, @AuthenticationPrincipal AuthUser authUser) {
        // 요청자가 관리자 권한을 가지고 있는지 확인
        if (authUser.getUserRole() != UserRole.ROLE_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("ACCESS_DENIED", "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다.")
            );
        }

        User updatedUser = authService.grantAdminRole(userId);

        ChangeRoleSuccess response = new ChangeRoleSuccess(
                updatedUser.getUsername(),
                updatedUser.getNickname(),
                List.of(new ChangeRoleSuccess.RoleDto(updatedUser.getRole().name()))
        );
        return ResponseEntity.ok(response);
    }
}
