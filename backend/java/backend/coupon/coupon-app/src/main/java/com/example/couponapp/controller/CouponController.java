package com.example.couponapp.controller;

import com.example.couponapp.dto.IssueRequestDto;
import com.example.couponapp.dto.ResponseDto;
import com.example.couponapp.dto.Status;
import com.example.couponapp.service.VerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class CouponController {

    private final VerificationService verificationService;

    @PostMapping("/issue")
    public Mono<ResponseEntity<ResponseDto>> issue(IssueRequestDto issueRequestDto) {
        verificationService.checkPeriodAndTime(issueRequestDto);
        verificationService.checkCouponInventory(issueRequestDto);
        verificationService.checkDuplicateIssue(issueRequestDto);
        return Mono.just(ResponseEntity.ok(new ResponseDto(Status.SUCCESSFUL, "")));
    }
}
