package com.sparta.java_test.domain.user.service;

import com.sparta.java_test.config.JwtUtil;
import com.sparta.java_test.domain.user.dto.SignRequest;
import com.sparta.java_test.domain.user.dto.SignResponse;
import com.sparta.java_test.domain.user.dto.SignupRequest;
import com.sparta.java_test.domain.user.dto.SignupResponse;
import com.sparta.java_test.domain.user.entity.User;
import com.sparta.java_test.domain.user.entity.UserRole;
import com.sparta.java_test.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.sparta.java_test.domain.user.entity.UserRole.ROLE_USER;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public SignupResponse signup(SignupRequest signupRequest) {

        String username = signupRequest.getUsername();
        String nickname = signupRequest.getNickname();

        String encodedPassword = passwordEncoder.encode(signupRequest.getPassword());
        UserRole role = ROLE_USER;

        User user = User.builder()
                .username(username)
                .password(encodedPassword)
                .nickname(nickname)
                .role(role)
                .build();

        userRepository.save(user);

        String token = jwtUtil.createToken(user);

        return SignupResponse.builder().role(role).build();
    }

    public SignResponse sign(SignRequest signRequest) {
        User user = userRepository.findByUsername(signRequest.getUsername()).orElseThrow(() -> new IllegalArgumentException("가입되지 않은 사용자입니다."));

        if (!passwordEncoder.matches(signRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String token = jwtUtil.createToken(user);

        return SignResponse.builder().token(token).build();
    }
}
