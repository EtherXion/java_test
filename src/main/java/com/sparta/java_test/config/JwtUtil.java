package com.sparta.java_test.config;

import com.sparta.java_test.domain.user.entity.AuthUser;
import com.sparta.java_test.domain.user.entity.User;
import com.sparta.java_test.domain.user.entity.UserRole;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final long TOKEN_TIME = 60 * 60 * 1000L; // 60분
    private static final long Refresh_TOKEN_TIME = 60 * 60 * 1000L;
    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public String createToken(User user) {
        Date now = new Date();

        Date expirationTime = new Date(now.getTime() + TOKEN_TIME);

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(String.valueOf(user.getId()))
                        .claim("email", user.getUsername())
                        .claim("userRole", user.getRole())
                        .setExpiration(expirationTime)
                        .setIssuedAt(now) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }
    // 리프레시 토큰
    public String createRefreshToken(String username, UserRole role) {
        Date date = new Date();

        return BEARER_PREFIX +
                Jwts.builder()
                        .setSubject(username) // 사용자 식별자값(ID)
                        .claim("userRole", role.name()) // 사용자 권한
                        .setExpiration(new Date(date.getTime() + Refresh_TOKEN_TIME)) // 만료 시간
                        .setIssuedAt(date) // 발급일
                        .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                        .compact();
    }

    public String substringToken(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        throw new IllegalArgumentException("Not Found Token");
    }

    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public AuthUser getAuthUserFromClaims(Claims claims) {
        long userId = Long.parseLong(claims.getSubject());
        String username = claims.get("username", String.class);
        String userRole = claims.get("userRole", String.class);

        return new AuthUser(userId, username, UserRole.valueOf(userRole));
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        return request.getHeader("Authorization");
    }
}
