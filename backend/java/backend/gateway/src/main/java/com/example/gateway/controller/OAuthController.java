//package com.example.gateway.controller;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
//import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
//import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
//import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.json.jackson2.JacksonFactory;
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.auth.oauth2.UserCredentials;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/login")
//@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
//public class OAuthController {
//
//    @Value("${google.client.id}")
//    private String CLIENT_ID;
//
//    @Value("${google.client.secret}")
//    private String CLIENT_SECRET;
//
//    private static final String REDIRECT_URI = "http://127.0.0.1:8080/oauth";
//
//    @PostMapping("/request")
//    public ResponseEntity<?> getAuthUrl() {
//        String authUrl = new GoogleAuthorizationCodeFlow.Builder(
//                new NetHttpTransport(),
//                JacksonFactory.getDefaultInstance(),
//                CLIENT_ID,
//                CLIENT_SECRET,
//                Arrays.asList("https://www.googleapis.com/auth/userinfo.profile", "openid"))
//                .setAccessType("offline")
//                .setApprovalPrompt("consent")
//                .build()
//                .newAuthorizationUrl()
//                .setRedirectUri(REDIRECT_URI)
//                .build();
//
//        Map<String, String> response = new HashMap<>();
//        response.put("url", authUrl);
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/oauth")
//    public ResponseEntity<?> handleOAuthResponse(@RequestParam("code") String code) {
//        try {
//            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
//                    new NetHttpTransport(),
//                    JacksonFactory.getDefaultInstance(),
//                    "https://oauth2.googleapis.com/token",
//                    CLIENT_ID,
//                    CLIENT_SECRET,
//                    code,
//                    REDIRECT_URI)
//                    .execute();
//
//            String accessToken = tokenResponse.getAccessToken();
//            String refreshToken = tokenResponse.getRefreshToken();
//
//            getUserData(accessToken);
//
//            // Here you might want to save the tokens or do something with them
//
//            return ResponseEntity.status(HttpStatus.FOUND)
//                    .header("Location", "http://localhost:5173/")
//                    .build();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred");
//        }
//    }
//
//    private void getUserData(String accessToken) {
//        RestTemplate restTemplate = new RestTemplate();
//        String userInfoEndpoint = "https://www.googleapis.com/oauth2/v3/userinfo?access_token=" + accessToken;
//
//        ResponseEntity<String> response = restTemplate.getForEntity(userInfoEndpoint, String.class);
//        System.out.println("User data: " + response.getBody());
//    }
//}