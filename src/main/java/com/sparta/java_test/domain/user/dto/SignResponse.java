package com.sparta.java_test.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class SignResponse {
    private String token;

    @Builder
    private SignResponse(String token) {this.token = token;}
}
