package com.example.websocketserver.decoder;

import com.example.websocketserver.config.JwtUriComponent;
import com.example.websocketserver.data.entity.Account;
import com.example.websocketserver.data.repository.AccountRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.Map;

/**
 * alg: 서명 알고리즘. "RS256"은 RSA-SHA256을 의미합니다. 이는 비대칭 암호화 알고리즘입니다.
 * kid: Key ID. 토큰 서명에 사용된 키를 식별합니다. 여러 키를 관리할 때 유용합니다.
 * typ: 토큰 타입. "JWT"는 이것이 JSON Web Token임을 나타냅니다.
 * <p>
 * iss (Issuer): 토큰 발급자. 여기서는 Google의 계정 서비스입니다.
 * azp (Authorized Party): 토큰을 요청한 클라이언트의 ID입니다.
 * aud (Audience): 이 토큰의 의도된 수신자. 여기서는 클라이언트 ID와 동일합니다.
 * sub (Subject): 사용자의 고유 식별자입니다.
 * email: 사용자의 이메일 주소입니다.
 * email_verified: 이메일 주소가 확인되었는지 여부입니다.
 * nbf (Not Before): 이 시간 이전에는 토큰이 유효하지 않습니다.
 * name, given_name, family_name: 사용자의 이름 정보입니다.
 * picture: 사용자의 프로필 사진 URL입니다.
 * iat (Issued At): 토큰이 발급된 시간입니다.
 * exp (Expiration Time): 토큰의 만료 시간입니다.
 * jti (JWT ID): 이 토큰의 고유 식별자입니다.
 * <p>
 * OAuth 2.0:
 * 이 프로토콜은 인증과 권한 부여를 위한 프레임워크입니다.
 * azp와 aud 클레임은 OAuth 2.0 클라이언트와 관련이 있습니다.
 * exp와 iat는 토큰의 수명을 제어합니다.
 * <p>
 * <p>
 * OpenID Connect:
 * OAuth 2.0의 확장으로, 인증에 중점을 둡니다.
 * sub 클레임은 OpenID Connect의 핵심 클레임으로, 사용자를 고유하게 식별합니다.
 * email, email_verified, name, given_name, family_name, picture는 OpenID Connect의 표준 클레임으로, 사용자에 대한 추가 정보를 제공합니다.
 */
@Component
@Slf4j
public class WebSocketJwtAuthenticationFilter {
    private Map<String, String> issuerToJwksUri;
    private final JwtUriComponent jwtUriComponent;
    private final RestTemplate restTemplate;

    private final AccountRepository accountRepository;

    public WebSocketJwtAuthenticationFilter(RestTemplate restTemplate, JwtUriComponent jwtUriComponent1, AccountRepository accountRepository) {
        this.restTemplate = restTemplate;
        this.jwtUriComponent = jwtUriComponent1;
        this.accountRepository = accountRepository;
        this.issuerToJwksUri = jwtUriComponent.getIssuerToJwksUri();
    }

    //TODO 실제로는 oauth2 subject 이 아닌 user id 를 리턴해야함.
    // 만일 서로다른 auth2 server 가 같은 sub id 를 가지면 문제 될 수 있기 때문.
    public String validateTokenAndReturnUserId(String token) {
        String userId = null;
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new Exception("Invalid token format");
            }
            // base64 로 cert 없이 디코딩 가능
            String header = new String(Base64.getUrlDecoder().decode(parts[0]));
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));

            log.info("header: {}, payload: {}", header, payload);

            JsonNode headerNode = new ObjectMapper().readTree(header);
            JsonNode payloadNode = new ObjectMapper().readTree(payload);

            String kid = headerNode.get("kid").asText();
            String issuer = payloadNode.get("iss").asText();
            String aud = payloadNode.get("aud").asText();
            String sub = payloadNode.get("sub").asText();

            Account account = accountRepository.findByOauth2Sub(sub).orElseThrow(() -> new Exception("cannot find User by its subject"));
            userId = account.getId().toString();

            log.info("kid: {}, issuer: {}", kid, issuer);
//            String jwksUri = issuerToJwksUri.get(issuer);
//            if (jwksUri == null) {
//                throw new Exception("Unknown issuer");
//            }
        } catch (Exception e) {
            log.error("Failed to parse token", e);
            return null;
        }
        return userId;
    }
}