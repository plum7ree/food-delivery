package com.example.couponapp.controller;

import com.example.couponapp.dto.IssueRequestDto;
import com.example.couponapp.dto.ResponseDto;
import com.example.couponapp.dto.Status;
import com.example.couponapp.service.KafkaProducerService;
import com.example.couponapp.service.VerificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WebFluxTest(CouponController.class)
public class CouponControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private VerificationService verificationService;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Test
    public void givenEveryThingWorks_returnSuccess() {
        // Given
        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("testUser");
        requestDto.setCouponId(123L);

        when(verificationService.checkLocalCache(any())).thenReturn(Mono.just(true));
        when(verificationService.checkPeriodAndTime(any())).thenReturn(Mono.just(true));
        when(verificationService.checkCouponInventory(any())).thenReturn(Mono.just(true));
        when(verificationService.checkDuplicateIssue(any())).thenReturn(Mono.just(false));
        when(verificationService.issueCouponToUser(any())).thenReturn(Mono.just(true));
        doNothing().when(kafkaProducerService).sendCouponIssueRequest(any());

        // When & Then
        webTestClient.post()
            .uri("/api/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isOk()
            .expectBody(ResponseDto.class)
            .value(responseDto -> {
                assert responseDto.getStatus() == Status.SUCCESSFUL;
                assert responseDto.getMessage().equals("Coupon issued successfully");
            });

        // Verify that the required methods were called
        verify(verificationService).checkLocalCache(any());
        verify(verificationService).checkPeriodAndTime(any());
        verify(verificationService).checkCouponInventory(any());
        verify(verificationService).checkDuplicateIssue(any());
        verify(verificationService).issueCouponToUser(any());
        verify(kafkaProducerService).sendCouponIssueRequest(any());
    }

    @Test
    public void givenDuplicatedIssueTrue_returnFails() {
        // Given
        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("testUser");
        requestDto.setCouponId(123L);

        when(verificationService.checkLocalCache(any())).thenReturn(Mono.just(true));
        when(verificationService.checkPeriodAndTime(any())).thenReturn(Mono.just(true));
        when(verificationService.checkCouponInventory(any())).thenReturn(Mono.just(true));
        when(verificationService.checkDuplicateIssue(any())).thenReturn(Mono.just(true));
        when(verificationService.issueCouponToUser(any())).thenReturn(Mono.just(true));
        doNothing().when(kafkaProducerService).sendCouponIssueRequest(any());

        // When & Then
        webTestClient.post()
            .uri("/api/issue")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(ResponseDto.class)
            .value(responseDto -> {
                assert responseDto.getStatus() == Status.FAILED;
                assert responseDto.getMessage().equals("Duplicate issue");
            });

        verify(verificationService, never()).issueCouponToUser(any());
        verify(kafkaProducerService, never()).sendCouponIssueRequest(any());
    }

}