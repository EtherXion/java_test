package com.sparta.java_test.domain.auth;

import com.sparta.java_test.config.JwtUtil;
import com.sparta.java_test.domain.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;

class AuthController {

    @MockBean
    private AuthService authService;
    @MockBean
    private JwtUtil jwtUtil;


    @BeforeEach
    void setUp() {

    }

}
