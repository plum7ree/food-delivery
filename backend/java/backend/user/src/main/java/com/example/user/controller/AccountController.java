package com.example.user.controller;

import com.example.user.data.dto.AddressDto;
import com.example.user.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

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
}