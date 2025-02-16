package com.sparta.java_test.domain.user.controller;

import com.sparta.java_test.domain.user.dto.SignRequest;
import com.sparta.java_test.domain.user.dto.SignResponse;
import com.sparta.java_test.domain.user.dto.SignupRequest;
import com.sparta.java_test.domain.user.dto.SignupResponse;
import com.sparta.java_test.domain.user.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> join(@RequestBody SignupRequest signupRequest) {
        SignupResponse signupResponse = authService.signup(signupRequest);
        return ResponseEntity.ok(signupResponse);
    }

    @PostMapping("/sign")
    public ResponseEntity<SignResponse> login(@RequestBody SignRequest signRequest) {
        SignResponse signResponse = authService.sign(signRequest);
        return ResponseEntity.ok(signResponse);
    }

}
