package com.example.couponapp.controller;

import com.example.couponapp.dto.IssueRequestDto;
import com.example.couponapp.dto.ResponseDto;
import com.example.couponapp.dto.Status;
import com.example.couponapp.service.KafkaProducerService;
import com.example.couponapp.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
@Slf4j
public class CouponController {

    private final VerificationService verificationService;
    private final KafkaProducerService kafkaProducerService;

    @PostMapping("/issue")
    public Mono<ResponseEntity<ResponseDto>> issue(@RequestBody IssueRequestDto issueRequestDto) {
        log.info("got request: {}", issueRequestDto);
        return verificationService.checkLocalCache(issueRequestDto)
            .flatMap(isValid -> {
                if (Boolean.TRUE.equals(isValid)) {
                    return verificationService.checkPeriodAndTime(issueRequestDto);
                } else {
                    return Mono.error(new IllegalArgumentException("Invalid local cache"));
                }
            })
            .flatMap(isValid -> {
                if (Boolean.TRUE.equals(isValid)) {
                    return verificationService.checkCouponInventory(issueRequestDto);
                } else {
                    return Mono.error(new IllegalArgumentException("Invalid period or time"));
                }
            })
            .flatMap(isValid -> {
                if (Boolean.TRUE.equals(isValid)) {
                    return verificationService.checkDuplicateIssue(issueRequestDto);
                } else {
                    return Mono.error(new IllegalArgumentException("Insufficient inventory"));
                }
            })
            .flatMap(isDuplicate -> {
                if (Boolean.FALSE.equals(isDuplicate)) {
                    return verificationService.issueCouponToUser(issueRequestDto);
                } else {
                    return Mono.error(new IllegalArgumentException("Duplicate issue"));
                }
            })
            .flatMap(isIssueSuccessful -> {
                if (Boolean.FALSE.equals(isIssueSuccessful)) {
                    return Mono.error(new IllegalArgumentException("Coupon issued error"));
                }
                return kafkaProducerService.sendCouponIssueRequest(issueRequestDto);
            })
            .map(status -> {
                if (status) {
                    // Webflux 가 Mono 로 감쌈.
                    return ResponseEntity.ok(new ResponseDto(Status.SUCCESSFUL, "Coupon issued successfully"));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ResponseDto(Status.FAILED, "Failed to process coupon issue"));
                }
            })
            .onErrorResume(e -> {
                String errorMessage = e instanceof IllegalArgumentException ? e.getMessage() : "Internal server error: " + e.getMessage();
                return Mono.just(ResponseEntity.badRequest().body(new ResponseDto(Status.FAILED, errorMessage)));
            });
    }
}
