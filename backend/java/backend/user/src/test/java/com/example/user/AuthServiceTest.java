package com.example.user;

import com.example.commondata.constants.ApplicationConstants;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AuthServiceTest {
    @Test
    public void jwtEncryptDecrypt_ShouldThrow() {
        String accessTokenSecret = ApplicationConstants.JWT_ACCESS_TOKEN_SECRET_DEFAULT_VALUE;
        SecretKey accessTokenSecretKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));

        String wrongTokenSecret = ApplicationConstants.JWT_REFRESH_TOKEN_SECRET_DEFAULT_VALUE;
        SecretKey wrongTokenSecretKey = Keys.hmacShaKeyFor(wrongTokenSecret.getBytes(StandardCharsets.UTF_8));
        String wrongTokenStr = Jwts.builder().issuer("food-delivery").subject("jwt-token")
            .claim("email", "user1.example.com")
            .claim("authorities", "ROLE_USER")
            .issuedAt(new java.util.Date())
            .expiration(new java.util.Date((new java.util.Date()).getTime() + 60 * 60 * 1000L)) // 1시간
            .signWith(wrongTokenSecretKey).compact();


        // JWT 서명 검증
        assertThrows(Exception.class, () -> {
            Jwts.parser().verifyWith(accessTokenSecretKey).build().parseClaimsJws(wrongTokenStr);
        });

    }

    @Test
    public void jwtEncryptDecrypt_NotThrow() {
        String accessTokenSecret = ApplicationConstants.JWT_ACCESS_TOKEN_SECRET_DEFAULT_VALUE;
        SecretKey accessTokenSecretKey = Keys.hmacShaKeyFor(accessTokenSecret.getBytes(StandardCharsets.UTF_8));
        String accessTokenStr = Jwts.builder().issuer("food-delivery").subject("jwt-token")
            .claim("email", "user1.example.com")
            .claim("authorities", "ROLE_USER")
            .issuedAt(new java.util.Date())
            .expiration(new java.util.Date((new java.util.Date()).getTime() + 60 * 60 * 1000L)) // 1시간
            .signWith(accessTokenSecretKey).compact();

        // JWT 서명 검증
        assertDoesNotThrow(() -> {
            Jwts.parser().verifyWith(accessTokenSecretKey).build().parseClaimsJws(accessTokenStr);
        });

    }

}
