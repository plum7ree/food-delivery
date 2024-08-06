package com.example.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Slf4j
public class UserServiceAuthenticationFilter implements GatewayFilter {

    // JwtDecoder.fromIssuerUri 는 굳이 필요없는듯?
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
            .filter(ctx -> ctx.getAuthentication() instanceof JwtAuthenticationToken)
            .map(ctx -> (JwtAuthenticationToken) ctx.getAuthentication())
            .map(auth -> {
                exchange.getRequest().mutate()
                    .headers(headers -> {
                        headers.add("X-Auth-User-Sub", auth.getToken().getSubject());
                        String email = auth.getToken().getClaimAsString("email");
                        headers.add("X-Auth-User-Roles", auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","))); // ["ROLE_USER", "ROLE_ADMIN", ...]
                        headers.add("X-Auth-User-Email", email);
                        headers.add("X-Auth-User-Provider", "GOOGLE");
                        // log.info("authorities: {}", auth.getAuthorities()); // [ROLE_USER]
                        // log.info("token claim: {}", auth.getToken().getClaims()); //  token claim: {sub=...,email, given_name, picture, aud, name, exp, ...}
                        // log.info("headers :{}", headers);
                    })
                    .build();

                return exchange;
            })
            .defaultIfEmpty(exchange)
            .flatMap(chain::filter);
    }
}