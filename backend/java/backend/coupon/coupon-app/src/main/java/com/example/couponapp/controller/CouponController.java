package com.example.couponapp.controller;

import com.example.couponapp.dto.IssueRequestDto;
import com.example.couponapp.dto.ResponseDto;
import com.example.couponapp.dto.Status;
import com.example.couponapp.service.KafkaProducerService;
import com.example.couponapp.service.VerificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                if (!Boolean.TRUE.equals(isValid)) {
                    return Mono.just(ResponseEntity.badRequest().body(new ResponseDto(Status.FAILED, "Invalid local cache")));
                }
                return verificationService.checkPeriodAndTime(issueRequestDto);
            })
            .flatMap(isValid -> {
                if (!Boolean.TRUE.equals(isValid)) {
                    return Mono.just(ResponseEntity.badRequest().body(new ResponseDto(Status.FAILED, "Invalid period or time")));
                }
                return verificationService.checkCouponInventory(issueRequestDto);
            })
            .flatMap(isValid -> {
                if (!Boolean.TRUE.equals(isValid)) {
                    return Mono.just(ResponseEntity.badRequest().body(new ResponseDto(Status.FAILED, "Insufficient inventory")));
                }
                return verificationService.checkDuplicateIssue(issueRequestDto);
            })
            .flatMap(isDuplicate -> {
                if (Boolean.TRUE.equals(isDuplicate)) {
                    return Mono.just(ResponseEntity.badRequest().body(new ResponseDto(Status.FAILED, "Duplicate issue")));
                }
                kafkaProducerService.sendCouponIssueRequest(issueRequestDto);

                return Mono.just(ResponseEntity.ok(new ResponseDto(Status.SUCCESSFUL, "Coupon issued successfully")));
            })
            .onErrorResume(e -> {
                // 에러 발생 시 에러 응답 반환
                return Mono.just(ResponseEntity.status(500).body(new ResponseDto(Status.FAILED, "Internal server error: " + e.getMessage())));
            });
    }
}
