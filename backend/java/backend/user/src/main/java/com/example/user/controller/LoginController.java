package com.example.user.controller;

import com.example.user.data.dto.UserDto;
import com.example.user.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api")
@Slf4j
public class LoginController {

    private final AccountService accountService;

    public LoginController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@RequestHeader HttpHeaders headers, @RequestBody UserDto userDto) throws ParseException {

        var email = Objects.requireNonNull(headers.get("X-Auth-User-Email")).get(0);
        var oauth2Sub = Objects.requireNonNull(headers.get("X-Auth-User-Sub")).get(0);
        var oauth2Provider = Objects.requireNonNull(headers.get("X-Auth-User-Provider")).get(0);
        var role = Objects.requireNonNull(headers.get("X-Auth-User-Roles")).get(0);

        userDto.setId(UUID.randomUUID().toString());
        userDto.setEmail(email);
        userDto.setRole(role);
        userDto.setOauth2Provider(oauth2Provider);
        userDto.setOauth2Sub(oauth2Sub);

        log.info("register userDto: {}", userDto);
        return Optional.of(accountService.register(userDto))
            .map(result -> result
                ? ResponseEntity.ok(userDto)
                : ResponseEntity.badRequest().body(userDto))
            .orElse(ResponseEntity.internalServerError().body(null));

    }

    @GetMapping("/info")
    public ResponseEntity<UserDto> getUserInfo(@RequestHeader HttpHeaders headers) throws ParseException {

        var email = Objects.requireNonNull(headers.get("X-Auth-User-Email")).get(0);

        return accountService.getUser(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.ok().body(UserDto.builder().id("").build()));

    }


    @GetMapping("/test")
    public ResponseEntity<String> test(@RequestHeader HttpHeaders headers) {
        headers.forEach((key, value) -> {
            log.info(String.format("Header '%s' = %s", key, value));
        });

        // 여기서 헤더와 jsonBody를 처리

        return ResponseEntity.ok("Headers processed");
    }
}