package com.example.couponapp.service;

import com.example.couponapp.dto.IssueRequestDto;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaProducer<String, CouponIssueRequestAvroModel> kafkaProducer;

    public Mono<Boolean> sendCouponIssueRequest(IssueRequestDto issueRequestDto) {
        CouponIssueRequestAvroModel message = CouponIssueRequestAvroModel.newBuilder()
            .setIssueId(System.currentTimeMillis())  // 예제용 ID 생성
            .setCallerId("some-uuid-caller-id")  // 실제 데이터로 대체
            .setCouponId(issueRequestDto.getCouponId())
            .setAmount(1L)
            .setCreatedAt(Instant.ofEpochSecond(Instant.now().toEpochMilli()))
            .build();

        return Mono.create(sink ->
            kafkaProducer.sendAndRunCallbackOnAck("coupon-issue-topic", "key", message,
                (metadata, exception) -> {
                    if (exception == null) {
                        sink.success(true);
                    } else {
                        sink.success(false);
                    }
                }
            )
        );
    }

}
