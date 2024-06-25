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
import static org.mockito.Mockito.when;

@WebFluxTest(CouponController.class)
public class CouponControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private VerificationService verificationService;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @Test
    public void testIssueEndpoint() {
        // Given
        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("testUser");
        requestDto.setCouponId(123L);

        when(verificationService.checkLocalCache(any())).thenReturn(Mono.just(true));
        when(verificationService.checkPeriodAndTime(any())).thenReturn(Mono.just(true));
        when(verificationService.checkCouponInventory(any())).thenReturn(Mono.just(true));
        when(verificationService.checkDuplicateIssue(any())).thenReturn(Mono.just(true));

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
    }

    @Test
    public void testIssueEndpointWithInvalidLocalCache() {
        // Given
        IssueRequestDto requestDto = new IssueRequestDto();
        requestDto.setUserId("testUser");
        requestDto.setCouponId(123L);

        when(verificationService.checkLocalCache(any())).thenReturn(Mono.just(false));

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
                assert responseDto.getMessage().equals("Invalid local cache");
            });
    }
}