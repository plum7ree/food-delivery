package com.example.eatsorderdataaccess.repository;


import com.example.commondata.domain.events.order.OutboxStatus;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class R2DBCRestaurantApprovalRequestOutboxRepository implements RestaurantApprovalRequestOutboxRepository {

    private final DatabaseClient databaseClient;

    public R2DBCRestaurantApprovalRequestOutboxRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<RestaurantApprovalOutboxMessageEntity> upsert(RestaurantApprovalOutboxMessageEntity entity) {
        String query = "INSERT INTO restaurant_approval_outbox (id, correlation_id, status) " +
            "VALUES (:id, :correlation_id, :status) " +
            "ON CONFLICT (id) DO UPDATE SET " +
            // 필요한 필드만 업데이트 한다.
            "status = :status";

        return databaseClient.sql(query)
            .bind("id", entity.getId())
            .bind("correlation_id", entity.getCorrelationId())
            .bind("status", entity.getStatus())
            .fetch()
            .rowsUpdated()
            .flatMap(rowsUpdated -> rowsUpdated > 0 ? Mono.just(entity) : Mono.empty());
    }


    @Override
    public Mono<RestaurantApprovalOutboxMessageEntity> findById(UUID id) {
        String query = "SELECT * FROM restaurant_approval_outbox WHERE id = :id";
        return databaseClient.sql(query)
            .bind("id", id)
            .map(row -> {
                var entity = RestaurantApprovalOutboxMessageEntity.builder()
                    .id(row.get("id", UUID.class))
                    .correlationId(row.get("correlation_id", UUID.class))
                    .status(row.get("outbox_status", String.class))
                    .build();
                return entity;
            }).one();

    }

    @Override
    public Mono<Boolean> updateStatus(UUID orderId, OutboxStatus outboxStatus) {
        String query = "UPDATE restaurant_approval_outbox SET id = :id, correlation_id = :correlation_id, status = :status";
        return databaseClient.sql(query)
            .bind("id", UUID.randomUUID())
            .bind("correlation_id", orderId)
            .bind("status", outboxStatus.name())
            .fetch()
            .rowsUpdated()
            .thenReturn(true);
    }

    @Override
    public Flux<RestaurantApprovalOutboxMessageEntity> findBySagaTypeAndOutboxStatusAndSagaStatusIn(String sagaType, String outboxStatus, String[] sagaStatuses) {
        return null;
    }

    @Override
    public Mono<RestaurantApprovalOutboxMessageEntity> findBySagaTypeAndOutboxStatus(String sagaType, String status) {
        return null;
    }
//
//    @Override
//    public Flux<RestaurantApprovalOutboxMessageEntity> findBySagaTypeAndOutboxStatusAndSagaStatusIn(
//        String sagaType,
//        String outboxStatus,
//        String[] sagaStatuses) {
//
//        String query = "SELECT * FROM restaurant_approval_outbox " +
//            "WHERE saga_type = :sagaType " +
//            "AND outbox_status = :outboxStatus " +
//            "AND saga_status IN (:sagaStatuses)";
//
//        return databaseClient.sql(query)
//            .bind("sagaType", sagaType)
//            .bind("outboxStatus", outboxStatus)
//            .bind("sagaStatuses", Arrays.asList(sagaStatuses))  // Convert array to list
//            .map(row -> {
//                var entity = RestaurantApprovalOutboxMessageEntity.builder()
//                    .id(row.get("id", UUID.class))
//                    .correlationId(row.get("correlation_id", UUID.class))
//                    .status(row.get("outbox_status", String.class))
//                    .build();
//                return entity;
//            })
//            .all();  // Fetches all matching rows
//    }
//
//    @Override
//    public Mono<RestaurantApprovalOutboxMessageEntity> findBySagaTypeAndOutboxStatus(
//        String sagaType,
//        String status) {
//
//        String query = "SELECT * FROM restaurant_approval_outbox " +
//            "WHERE saga_type = :sagaType AND outbox_status = :status " +
//            "LIMIT 1";
//
//        return databaseClient.sql(query)
//            .bind("sagaType", sagaType)
//            .bind("status", status)
//            .map(row -> {
//                var entity = RestaurantApprovalOutboxMessageEntity.builder()
//                    .id(row.get("id", UUID.class))
//                    .correlationId(row.get("correlation_id", UUID.class))
//                    .status(row.get("outbox_status", String.class))
//                    .build();
//                return entity;
//            })
//            .one();  // Fetches one matching row
//    }
}
