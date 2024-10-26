package com.example.gateway.security;

import com.example.commondata.constants.ApplicationConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class JwtVerificationFilter implements WebFilter {

    private final Environment env;

    public JwtVerificationFilter(Environment env) {
        this.env = env;
    }

    private String resolveToken(ServerWebExchange exchange) {
        String bearerToken = exchange.getRequest().getHeaders()
            .getFirst(HttpHeaders.AUTHORIZATION);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String jwtTokenStr = resolveToken(exchange);
        if (jwtTokenStr != null) {
            String secret = env.getProperty(
                ApplicationConstants.JWT_ACCESS_TOKEN_SECRET_KEY,
                ApplicationConstants.JWT_ACCESS_TOKEN_SECRET_DEFAULT_VALUE);
            SecretKey secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
            try {
                // JWT 서명 검증
                Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwtTokenStr);


                // 검증 성공 시 SecurityContext에 Authentication 설정
                UserDetails userDetails = User.withUsername("user")
                    .password("") // 비밀번호는 필요 없음
                    .authorities("ROLE_USER") // 필요한 권한 설정
                    .build();
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                SecurityContextImpl securityContext = new SecurityContextImpl(authentication);

                return chain.filter(exchange)
                    .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
            } catch (Exception e) {
                // 유효하지 않은 JWT인 경우, 요청을 차단
                log.error("JwtVerificationFilter.filter error {}", e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
        }
        // JWT가 존재하지 않을 경우 다음 필터로 전달
        return chain.filter(exchange);
    }
}

