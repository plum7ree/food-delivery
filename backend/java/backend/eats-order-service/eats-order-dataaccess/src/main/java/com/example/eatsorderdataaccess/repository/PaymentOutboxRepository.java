package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.PaymentOutboxMessageEntity;
import reactor.core.publisher.Mono;

public interface PaymentOutboxRepository {

    Mono<PaymentOutboxMessageEntity> upsert(PaymentOutboxMessageEntity entity);

    Mono<PaymentOutboxMessageEntity> findBySagaIdAndSagaStatus(String sagaId, String sagaStatus);
}
