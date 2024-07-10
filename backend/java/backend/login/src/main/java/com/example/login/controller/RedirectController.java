package com.example.login.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class RedirectController {
    @Value("${frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @GetMapping("/")
    public void redirectToFrontend(HttpServletResponse response) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User user = oauthToken.getPrincipal();

            // JWT 토큰 생성
            String jwtToken = generateJwtToken(user);

            // 쿠키에 JWT 토큰 저장
            Cookie cookie = new Cookie("AUTH_TOKEN", jwtToken);
            cookie.setPath("/");
            cookie.setMaxAge(3600); // 1시간
            cookie.setHttpOnly(true);
            // cookie.setSecure(true); // HTTPS 사용 시 활성화
            response.addCookie(cookie);
        }

        // 프론트엔드로 리다이렉트
        response.sendRedirect(frontendUrl);
    }
    // 다른 엔드포인트...

    private String generateJwtToken(OAuth2User user) {
        // JWT 토큰 생성 로직 구현
        // 이 부분은 실제 JWT 생성 로직으로 대체해야 합니다.
        return "sample-token";
    }
}