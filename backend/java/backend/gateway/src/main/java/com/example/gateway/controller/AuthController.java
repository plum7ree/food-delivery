package com.example.gateway.controller;

import com.example.gateway.dto.AuthResponse;
import com.google.api.client.auth.oauth2.TokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson.JacksonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Collections;

@RestController
@Slf4j
public class AuthController {

    @GetMapping("/test")
    public Mono<ResponseEntity<?>> authenticateGoogle() {
        try {
            log.info("test request");
            return Mono.just(ResponseEntity.ok().build());
        } catch (Exception e) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
    }


}