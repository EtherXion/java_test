package com.sparta.java_test.domain.user.dto;

import lombok.Getter;

@Getter
public class SignupRequest {
    private String username;
    private String password;
    private String nickname;
}