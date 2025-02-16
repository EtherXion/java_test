package com.sparta.java_test.domain.user.dto;

import com.sparta.java_test.domain.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class SignupResponse {
    private String username;
    private String nickname;
    private List<AuthorityDto> authorities;

    @Getter
    public static class AuthorityDto {
        private String authorityName;

        public AuthorityDto(UserRole role) {
            this.authorityName = role.name();
        }
    }

    @Builder
    private SignupResponse(String username, String nickname, UserRole role) {
        this.username = username;
        this.nickname = nickname;
        this.authorities = Collections.singletonList(new AuthorityDto(role));
    }
}
