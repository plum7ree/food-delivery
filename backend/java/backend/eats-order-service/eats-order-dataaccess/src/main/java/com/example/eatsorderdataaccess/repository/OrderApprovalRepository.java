package com.example.eatsorderdataaccess.repository;


import com.example.eatsorderdataaccess.entity.OrderApprovalEntity;
import reactor.core.publisher.Mono;
import java.util.UUID;

public interface OrderApprovalRepository {

    Mono<OrderApprovalEntity> findByOrderIdAndStatus(UUID orderId, String status);

    Mono<Void> upsert(OrderApprovalEntity entity);

    Mono<OrderApprovalEntity> save(OrderApprovalEntity entity);
}
