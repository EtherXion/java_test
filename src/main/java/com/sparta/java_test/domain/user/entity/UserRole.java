package com.sparta.java_test.domain.user.entity;

import java.util.Arrays;

public enum UserRole {
    ROLE_USER, ROLE_ADMIN;

    public static UserRole of(String role) {
        return Arrays.stream(UserRole.values())
                .filter(r -> r.name().equalsIgnoreCase(role))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 UserRole"));
    }
}