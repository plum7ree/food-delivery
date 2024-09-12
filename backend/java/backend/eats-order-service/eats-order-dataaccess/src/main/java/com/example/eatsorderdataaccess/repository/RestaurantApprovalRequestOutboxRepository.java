package com.example.eatsorderdataaccess.repository;


import com.example.commondata.domain.events.order.OutboxStatus;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface RestaurantApprovalRequestOutboxRepository {

    Mono<RestaurantApprovalOutboxMessageEntity> upsert(RestaurantApprovalOutboxMessageEntity entity);

    Mono<RestaurantApprovalOutboxMessageEntity> findById(UUID id);

    Flux<RestaurantApprovalOutboxMessageEntity> findBySagaTypeAndOutboxStatusAndSagaStatusIn(
        String sagaType,
        String outboxStatus,
        String[] sagaStatuses);


    Mono<RestaurantApprovalOutboxMessageEntity> findBySagaTypeAndOutboxStatus(
        String sagaType,
        String status
    );

    Mono<Boolean> updateStatus(UUID orderId, OutboxStatus outboxStatus);
}
