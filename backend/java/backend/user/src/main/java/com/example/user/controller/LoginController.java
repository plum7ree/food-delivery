package com.example.user.controller;

import com.example.user.data.dto.AddressDto;
import com.example.user.data.dto.RegisterUserDto;
import com.example.user.data.dto.UserDto;
import com.example.user.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
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
    public ResponseEntity<String> register(@RequestHeader HttpHeaders headers, @RequestBody RegisterUserDto registerUserDto) throws ParseException {
        Assert.hasLength(registerUserDto.getUsername(), "name is empty");
        Assert.hasLength(registerUserDto.getLat(), "getLat is empty");
        Assert.hasLength(registerUserDto.getLon(), "getLon is empty");
        Assert.hasLength(registerUserDto.getStreet(), "getStreet is empty");
        Assert.hasLength(registerUserDto.getCity(), "getCity is empty");
        Assert.hasLength(registerUserDto.getPostalCode(), "getPostalCode is empty");

        var email = Objects.requireNonNull(headers.get("X-Auth-User-Email")).get(0);
        var oauth2Sub = Objects.requireNonNull(headers.get("X-Auth-User-Sub")).get(0);
        var oauth2Provider = Objects.requireNonNull(headers.get("X-Auth-User-Provider")).get(0);
        var role = Objects.requireNonNull(headers.get("X-Auth-User-Roles")).get(0);

        UserDto userDto = UserDto.builder().build();
        userDto.setId(UUID.randomUUID().toString());
        userDto.setEmail(email);
        userDto.setRole(role);
        userDto.setOauth2Provider(oauth2Provider);
        userDto.setOauth2Sub(oauth2Sub);
        userDto.setUsername(registerUserDto.getUsername());

        AddressDto addressDto = AddressDto.builder()
            .id(UUID.randomUUID().toString())
            .userId(userDto.getId()) // account 에서 id 직접 가져와야 foreign key error 안뜬다.
            .city(registerUserDto.getCity())
            .street(registerUserDto.getStreet())
            .postalCode(registerUserDto.getPostalCode())
            .lat(Double.parseDouble(registerUserDto.getLat()))
            .lon(Double.parseDouble(registerUserDto.getLon()))
            .build();

        log.info("register userDto: {} ", userDto);
        log.info("register addressDto: {}", addressDto);

        try {
            //TODO transactional
            // ADDRESS 는 restaurant / user one to one mapping 으로 되어있나? 어느걸로 되어있지 ?
            accountService.registerUserWithAddress(userDto, addressDto);
            return ResponseEntity.ok("");
        } catch (Exception e) {
            log.error("User registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/info")
    public ResponseEntity<UserDto> getUserInfo(@RequestHeader HttpHeaders headers) throws ParseException {

        var sub = Objects.requireNonNull(headers.get("X-Auth-User-Sub")).get(0);

        return accountService.getUserByOauth2Subject(sub)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.badRequest().body(null));

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