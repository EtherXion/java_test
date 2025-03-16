package com.sparta.java_test.domain.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.java_test.config.JwtUtil;
import com.sparta.java_test.domain.user.controller.AuthController;
import com.sparta.java_test.domain.user.dto.SignRequest;
import com.sparta.java_test.domain.user.dto.SignupRequest;
import com.sparta.java_test.domain.user.dto.SignResponse;
import com.sparta.java_test.domain.user.dto.SignupResponse;
import com.sparta.java_test.domain.user.entity.AuthUser;
import com.sparta.java_test.domain.user.entity.User;
import com.sparta.java_test.domain.user.entity.UserRole;
import com.sparta.java_test.domain.user.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthController.class)
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Mock
    private AuthService authService;
    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private AuthUser authUser;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void 회원가입_성공() throws Exception {
        SignupRequest signupRequest = new SignupRequest("username", "password", "nickname");
        SignupResponse signupResponse = SignupResponse.builder()
                .username("username")
                .nickname("nickname")
                .role(UserRole.ROLE_USER)
                .build();

        when(authService.signup(signupRequest)).thenReturn(signupResponse);

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("username"))
                .andExpect(jsonPath("$.nickname").value("nickname"))
                .andExpect(jsonPath("$.authorities[0].authorityName").value("USER"));
    }

    @Test
    void 회원가입_실패_이미_존재하는_사용자() throws Exception {
        SignupRequest signupRequest = new SignupRequest("username", "password", "nickname");

        when(authService.signup(signupRequest))
                .thenThrow(new IllegalArgumentException("이미 가입된 사용자입니다."));

        mockMvc.perform(post("/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errorCode").value("USER_ALREADY_EXISTS"));
    }

    @Test
    void 로그인_성공() throws Exception {
        SignRequest signRequest = new SignRequest("username", "password");
        SignResponse signResponse = SignResponse.builder()
                .token("jwt-token")
                .build();

        when(authService.sign(signRequest)).thenReturn(signResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bearerToken").value("jwt-token"));
    }

    @Test
    void 로그인_실패_유저명_불일치() throws Exception {
        SignRequest signRequest = new SignRequest("username", "wrong-password");

        when(authService.sign(signRequest))
                .thenThrow(new IllegalArgumentException("아이디 또는 비밀번호가 올바르지 않습니다."));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.errorCode").value("INVALID_CREDENTIALS"));
    }

    @Test
    void 권한부여_성공() throws Exception {
        Long userId = 1L;
        AuthUser adminUser = new AuthUser(1L, "admin", UserRole.ROLE_ADMIN);
        User updatedUser = User.builder()
                .username("user")
                .password("password")
                .nickname("nickname")
                .role(UserRole.ROLE_ADMIN)
                .build();
        when(authService.grantAdminRole(userId)).thenReturn(updatedUser);

        mockMvc.perform(patch("/admin/users/{userId}/roles", userId)
                        .header("Authorization", "Bearer jwt-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user"))
                .andExpect(jsonPath("$.nickname").value("nickname"))
                .andExpect(jsonPath("$.authorities[0].authorityName").value("ADMIN"));
    }

    @Test
    void 권한부여_실패_권한_부족() throws Exception {
        Long userId = 1L;
        AuthUser normalUser = new  AuthUser(1L,"user",UserRole.ROLE_USER);
        when(authService.grantAdminRole(userId)).thenThrow(new IllegalArgumentException("관리자 권한이 필요한 요청입니다."));

        mockMvc.perform(patch("/admin/users/{userId}/roles", userId)
                        .header("Authorization", "Bearer jwt-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.errorCode").value("ACCESS_DENIED"));
    }

}
