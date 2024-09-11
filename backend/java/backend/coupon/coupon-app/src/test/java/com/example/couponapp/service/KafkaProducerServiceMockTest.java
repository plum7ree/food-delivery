package com.example.couponapp.service;

import com.example.couponapp.dto.IssueRequestDto;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import com.example.kafkaproducer.GeneralKafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.BiConsumer;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doAnswer;

class KafkaProducerServiceMockTest {

    @Mock
    private GeneralKafkaProducer<String, CouponIssueRequestAvroModel> kafkaProducer;

    private KafkaProducerService kafkaProducerService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        kafkaProducerService = new KafkaProducerService(kafkaProducer);
    }

    @Test
    void testSendCouponIssueRequest_Success() {
        // Arrange
        IssueRequestDto issueRequestDto = new IssueRequestDto("testUser", 1000000L);

        doAnswer(invocation -> {
            var callback = invocation.getArgument(3, BiConsumer.class);
            callback.accept(null, null);  // Simulate successful ACK
            return null;
        }).when(kafkaProducer).sendAndRunCallback(anyString(), anyString(), any(), any());

        // Act
        Mono<Boolean> result = kafkaProducerService.sendCouponIssueRequest(issueRequestDto);

        // Assert
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    void testSendCouponIssueRequest_Failure() {
        // Arrange
        IssueRequestDto issueRequestDto = new IssueRequestDto("testUser", 1000000L);

        doAnswer(invocation -> {
            var callback = invocation.getArgument(3, BiConsumer.class);
            callback.accept(null, new Exception("Kafka error"));  // Simulate ACK failure
            return null;
        }).when(kafkaProducer).sendAndRunCallback(anyString(), anyString(), any(), any());

        // Act
        Mono<Boolean> result = kafkaProducerService.sendCouponIssueRequest(issueRequestDto);

        // Assert
        StepVerifier.create(result)
            .expectNext(false)
            .verifyComplete();
    }
}