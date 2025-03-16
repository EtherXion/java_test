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
    public ResponseEntity<SignupResponse> join(@RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = authService.signup(signupRequest);
        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<SignResponse> login(@RequestBody SignRequest signRequest) {
        SignResponse signResponse = authService.sign(signRequest);
        return ResponseEntity.ok(signResponse);
    }

    @PatchMapping("/admin/users/{userId}/roles")
    public ResponseEntity<?> grantAdminRole(@PathVariable Long userId, @AuthenticationPrincipal AuthUser authUser) {
        // 요청자가 관리자 권한을 가지고 있는지 확인
        if (authUser.getUserRole() != UserRole.ROLE_ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new ChangeRoleError(new ChangeRoleError.ErrorDetail("ACCESS_DENIED", "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."))
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
