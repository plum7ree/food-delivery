//package com.example.gateway.config;
//
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.core.Ordered;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.stereotype.Component;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.util.List;
//
///**
// * Spring Security 필터가 먼저 실행되고, 그 다음에 Gateway 필터
// */
//@Component
//@Slf4j
//public class JwtUrlToHeaderFilter implements GatewayFilter, Ordered {
//
//    private static final String TOKEN_PARAM = "token";
//    private static final String AUTH_HEADER = "Authorization";
//
//    /**
//     * http://localhost:8080/sockjs/order/status?token=${credential} 이렇게 생긴 url 에서
//     * credential 추출.
//     *
//     * @param exchange the current server exchange
//     * @param chain    provides a way to delegate to the next filter
//     * @return
//     */
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        ServerHttpRequest request = exchange.getRequest();
//        String token = extractTokenFromQuery(request);
//        log.info("got token: {}", token);
//        if (token != null) {
//            ServerHttpRequest modifiedRequest = request.mutate()
//                .header(AUTH_HEADER, "Bearer " + token)
//                .build();
//            log.info("modifiedRequest : {}", modifiedRequest.toString());
//
//            return chain.filter(exchange.mutate().request(modifiedRequest).build());
//        }
//
//        return chain.filter(exchange);
//    }
//
//    private String extractTokenFromQuery(ServerHttpRequest request) {
//        MultiValueMap<String, String> queryParams = request.getQueryParams();
//        List<String> tokenParams = queryParams.get(TOKEN_PARAM);
//        return (tokenParams != null && !tokenParams.isEmpty()) ? tokenParams.get(0) : null;
//    }
//
//    @Override
//    public int getOrder() {
//        return Ordered.HIGHEST_PRECEDENCE;
//    }
//}