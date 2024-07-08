//package com.example.gateway.controller;
//
//import com.example.gateway.dto.AuthResponse;
//import com.google.api.client.auth.oauth2.TokenRequest;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
//import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson.JacksonFactory;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.Collections;
//
//@RestController
//@Slf4j
//public class AuthController {
//    @Value("${spring.security.oauth2.client.registration.google.client-id}")
//    private String clientId;
//
//    @PostMapping("/api/auth/google")
//    public ResponseEntity<?> authenticateGoogle(@RequestBody TokenRequest tokenRequest) {
//        try {
//            log.info("authenticate Google tokenRequest: {}", tokenRequest);
//            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
//                .setAudience(Collections.singletonList(clientId))
//                .build();
//
//            GoogleIdToken idToken = verifier.verify(tokenRequest.toString());
//            if (idToken != null) {
//                GoogleIdToken.Payload payload = idToken.getPayload();
//
//                String userId = payload.getSubject();
//                String email = payload.getEmail();
//                boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//                String name = (String) payload.get("name");
//                String pictureUrl = (String) payload.get("picture");
//
//                // 여기서 사용자 정보를 데이터베이스에 저장하거나 업데이트할 수 있습니다.
//                // 그리고 자체 JWT 토큰을 생성하여 반환할 수 있습니다.
//                return ResponseEntity.ok(new AuthResponse(userId, email, name));
//            } else {
//                return ResponseEntity.badRequest().body("Invalid ID token.");
//            }
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().body("Token verification failed: " + e.getMessage());
//        }
//    }
//
//
//}