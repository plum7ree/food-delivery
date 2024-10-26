package com.example.user.controller;

import com.example.user.data.dto.web.AddressDto;
import com.example.user.data.dto.web.UserDto;
import com.example.user.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountService accountService; // 실제 사용자 정보를 조회하는 서비스


    @GetMapping("/locations")
    public List<AddressDto> findUserLatLonByIds(@RequestParam List<UUID> userIds) {
        Assert.notEmpty(userIds, "userIds is empty");
        userIds.forEach(id -> {
            Assert.notNull(id, "id is null");
        });
        return accountService.findAddressesByUserIds(userIds)
            .orElseGet(Collections::emptyList); // 데이터가 없을 경우 빈 리스트 반환
    }

    /**
     * /auth/login 에서 api 받은 후 맨 처음 유저에 대한 정보를 불러올 것이다.
     *
     * @param headers
     * @return
     * @throws ParseException
     */
    @GetMapping("/info")
    public ResponseEntity<UserDto> getUserInfo(@RequestHeader HttpHeaders headers) throws ParseException {

        log.info("headers: {}", headers);
//        var oauth2LoginTypeEmail = Objects.requireNonNull(headers.get("X-Auth-User-Email")).get(0);
//
//        return accountService.getUserByEmail(oauth2LoginTypeEmail)
//            .map(ResponseEntity::ok)
//            .orElse(ResponseEntity.badRequest().body(null));
        return ResponseEntity.ok(UserDto.builder().build());

    }
}