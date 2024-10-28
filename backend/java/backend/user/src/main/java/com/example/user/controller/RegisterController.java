package com.example.user.controller;

import com.example.user.data.dto.web.*;
import com.example.user.service.AccountService;
import com.example.user.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class RegisterController {

    private final AccountService accountService;
    private final RefreshTokenService refreshTokenService;

    private final Environment env;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterUserDto registerUserDto) {
        Assert.hasLength(registerUserDto.getEmail(), "email is empty");
        Assert.hasLength(registerUserDto.getUsername(), "name is empty");
        Assert.hasLength(registerUserDto.getPassword(), "password is empty");
        Assert.hasLength(registerUserDto.getLat(), "getLat is empty");
        Assert.hasLength(registerUserDto.getLon(), "getLon is empty");
        Assert.hasLength(registerUserDto.getStreet(), "getStreet is empty");
        Assert.hasLength(registerUserDto.getCity(), "getCity is empty");
        Assert.hasLength(registerUserDto.getPostalCode(), "getPostalCode is empty");
        UserDto userDto = UserDto.builder().build();
        userDto.setId(UUID.randomUUID().toString());
        userDto.setEmail(registerUserDto.getEmail());
        userDto.setUsername(registerUserDto.getUsername());
        userDto.setEncryptedPassword(passwordEncoder.encode(registerUserDto.getPassword()));
        userDto.setRole("USER");
        AddressDto addressDto = AddressDto.builder()
            .id(UUID.randomUUID().toString())
            .userId(userDto.getId()) // account 에서 id 직접 가져와야 foreign key error 안뜬다.
            .city(registerUserDto.getCity())
            .street(registerUserDto.getStreet())
            .postalCode(registerUserDto.getPostalCode())
            .lat(Double.parseDouble(registerUserDto.getLat()))
            .lon(Double.parseDouble(registerUserDto.getLon()))
            .build();

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


    @PostMapping("/oauth2/register")
    public ResponseEntity<String> register(@RequestHeader HttpHeaders headers, @RequestBody Oauth2RegisterUserDto registerUserDto) throws ParseException {
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


}