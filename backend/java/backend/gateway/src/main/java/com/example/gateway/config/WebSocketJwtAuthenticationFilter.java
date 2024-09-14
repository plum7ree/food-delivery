package com.example.gateway.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.jose4j.jwk.JsonWebKey;
import org.jose4j.jwk.JsonWebKeySet;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class WebSocketJwtAuthenticationFilter implements GatewayFilter, Ordered {

    private final WebClient webClient;
    private Map<String, String> issuerToJwksUri;
    private final JwtUriComponent jwtUriComponent;


    public WebSocketJwtAuthenticationFilter(WebClient.Builder webClientBuilder, JwtUriComponent jwtUriComponent1) {
        this.webClient = webClientBuilder.build();
        this.jwtUriComponent = jwtUriComponent1;
        this.issuerToJwksUri = jwtUriComponent.getIssuerToJwksUri();
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String token = extractTokenFromQuery(request);

        if (token == null) {
            return onError(exchange, "Missing token", HttpStatus.UNAUTHORIZED);
        }

        return validateToken(token)
            .flatMap(claims -> {
                ServerHttpRequest modifiedRequest = request.mutate()
                    .header("X-Auth-User", claims.getSubject())
                    .build();
                return chain.filter(exchange.mutate().request(modifiedRequest).build());
            })
            .onErrorResume(e -> onError(exchange, "Invalid token", HttpStatus.UNAUTHORIZED));
    }

    private Mono<Claims> validateToken(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            return Mono.error(new JwtException("Invalid token format"));
        }
        // base64 로 cert 없이 디코딩 가능
        String header = new String(Base64.getUrlDecoder().decode(parts[0]));
        String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

        log.info("header: {}, payload: {}", header, payload);


        try {
            JsonNode headerNode = new ObjectMapper().readTree(header);
            JsonNode payloadNode = new ObjectMapper().readTree(payload);

            String kid = headerNode.get("kid").asText();
            String issuer = payloadNode.get("iss").asText();
            String aud = payloadNode.get("aud").asText();

            log.info("kid: {}, issuer: {}", kid, issuer);
            String jwksUri = issuerToJwksUri.get(issuer);
            if (jwksUri == null) {
                return Mono.error(new JwtException("Unknown issuer"));
            }

            return webClient.get()
                .uri(jwksUri)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(jwksJson -> {
                    try {
                        // 공개키 세트
                        JsonWebKeySet jwks = new JsonWebKeySet(jwksJson);
                        // 공개키 검색:
                        JsonWebKey jwk = jwks.getJsonWebKeys().stream()
                            .filter(key -> kid.equals(key.getKeyId()))
                            .findFirst()
                            .orElseThrow(() -> new JwtException("Unable to find JWK with kid: " + kid));
                        // 공개키로 JWT 토큰을 검증
                        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                            .setVerificationKey(jwk.getKey())
                            .setExpectedAudience(aud)
//                            .setExpectedSubject(sub)
//                            .setExpectedType(type)
                            .setExpectedIssuer(issuer)
                            .build();


                        JwtClaims jwtClaims = jwtConsumer.processToClaims(token);
                        Claims claims = Jwts.claims()
                            .add("azp", jwtClaims.getClaimValueAsString("azp"))
                            .add("aud", jwtClaims.getClaimValueAsString("aud"))
                            .add("sub", jwtClaims.getClaimValueAsString("sub"))
                            .add("email", jwtClaims.getClaimValueAsString("email"))
                            .add("email_verified", jwtClaims.getClaimValueAsString("email_verified"))
                            .add("nbf", jwtClaims.getClaimValueAsString("nbf"))
                            .add("picture", jwtClaims.getClaimValueAsString("picture"))
                            .add("given_name", jwtClaims.getClaimValueAsString("given_name"))
                            .add("family_name", jwtClaims.getClaimValueAsString("family_name"))
                            .add("iat", jwtClaims.getClaimValueAsString("iat"))
                            .add("exp", jwtClaims.getClaimValueAsString("exp"))
                            .add("jti", jwtClaims.getClaimValueAsString("jti"))
                            .build();

                        log.info("jwtClaims: {}", claims);
                        return Mono.just(claims);
                    } catch (Exception e) {
                        return Mono.error(new JwtException("Failed to validate token", e));
                    }
                })
                .doOnNext(claims -> log.info("JWT validation successful. Claims: {}", claims))
                .doOnError(error -> log.error("JWT validation failed", error)); // 에러 확인좀

//            JWT (claims->{"iss":"https://accounts.google.com",
//                "azp":"",
//                "aud":"",
//                "sub":"",
//                "email":"",
//                "email_verified":true,
//                "nbf":,
//                "name":"",
//                "picture":"" + "A=",
//                "given_name":"",
//                "family_name":"",
//                "iat":1722317635,
//                "exp":1722321235,
//                "jti":""
//                })

        } catch (Exception e) {
            return Mono.error(new JwtException("Failed to parse token", e));
        }
    }

    private String extractTokenFromQuery(ServerHttpRequest request) {
        MultiValueMap<String, String> queryParams = request.getQueryParams();
        List<String> tokenParams = queryParams.get("token");
        return (tokenParams != null && !tokenParams.isEmpty()) ? tokenParams.get(0) : null;
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.writeWith(Mono.just(response.bufferFactory().wrap(err.getBytes())));
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}