package com.example.couponapp.service;

import com.example.couponapp.dto.IssueRequestDto;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {

    private final KafkaProducer<String, CouponIssueRequestAvroModel> kafkaProducer;

    // kafka producer 자체적인 retries 랑은 다르다.
    // kafka producer 자체적인 retries 가 n 이라고 하고, timeout 이고10ms 라고 하면,
    // 10ms 이내에 n 번을 보낼 수 도 있고 혹은 보내지 못하더라고 timeout 에러가 발생된다.
    // 이 코드는, 10ms 동안 응답이 안오면 발생되는 timeout 에러를 인지하, MAX_RETRIES 만큼 이 코드는 다시 보낸다.
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 10;

    public Mono<Boolean> sendCouponIssueRequest(IssueRequestDto issueRequestDto) {
        return retryMessagingWithFallback(issueRequestDto, 0);
    }

    private Mono<Boolean> retryMessagingWithFallback(IssueRequestDto issueRequestDto, int retryCount) {
        if (retryCount >= MAX_RETRIES) {
            return Mono.just(false);
        }
        // https://www.baeldung.com/java-uuid-unique-long-generation
        UUID issueId = UUID.randomUUID();
        long issueIdLong = (issueId.getMostSignificantBits() << 64) | issueId.getLeastSignificantBits();
        CouponIssueRequestAvroModel message = CouponIssueRequestAvroModel.newBuilder()
            .setIssueId(issueIdLong)
            .setCallerId(issueRequestDto.getUserId())
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
                        log.error("Failed to send message to Kafka, retrying...", exception);
                        Mono.delay(Duration.ofMillis(RETRY_DELAY_MS))
                            .then(retryMessagingWithFallback(issueRequestDto, retryCount + 1))
                            .subscribe(sink::success, sink::error);
                    }
                }
            )
        );
    }

}
