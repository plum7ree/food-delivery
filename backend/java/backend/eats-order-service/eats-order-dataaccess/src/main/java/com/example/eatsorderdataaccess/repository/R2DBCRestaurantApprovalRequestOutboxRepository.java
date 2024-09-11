package com.example.eatsorderdataaccess.repository;


import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.UUID;

@Repository
public class R2DBCRestaurantApprovalRequestOutboxRepository implements RestaurantApprovalRequestOutboxRepository {

    private final DatabaseClient databaseClient;

    public R2DBCRestaurantApprovalRequestOutboxRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<RestaurantApprovalOutboxMessageEntity> upsert(RestaurantApprovalOutboxMessageEntity entity) {
        String query = "INSERT INTO restaurant_approval_outbox (id, saga_id, saga_type, payload, order_status, outbox_status, saga_status, created_at, processed_at) " +
            "VALUES (:id, :saga_id, :sagaType, :payload, :orderStatus, :outboxStatus, :sagaStatus, :createdAt, :processedAt) " +
            "ON CONFLICT (id) DO UPDATE SET " +
            // 필요한 필드만 업데이트 한다.
            "saga_type = :sagaType, order_status = :orderStatus, outbox_status = :outboxStatus, saga_status = :sagaStatus, " +
            "payload = :payload, processed_at = :processedAt";

        return databaseClient.sql(query)
            .bind("id", entity.getId())
            .bind("saga_id", entity.getSagaId())
            .bind("sagaType", entity.getSagaType())
            .bind("payload", entity.getPayload())
            .bind("orderStatus", entity.getOrderStatus())
            .bind("outboxStatus", entity.getOutboxStatus())
            .bind("sagaStatus", entity.getSagaStatus())
            .bind("createdAt", entity.getCreatedAt())
            .bind("processedAt", entity.getProcessedAt())
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
                    .sagaType(row.get("saga_type", String.class))
                    .outboxStatus(row.get("outbox_status", String.class))
                    .sagaStatus(row.get("saga_status", String.class))
                    .payload(row.get("payload", String.class))  // Assuming there's a payload column
                    .createdAt(row.get("created_at", ZonedDateTime.class))
                    .processedAt(row.get("processed_at", ZonedDateTime.class))
                    .build();
                return entity;
            }).one();

    }

    @Override
    public Flux<RestaurantApprovalOutboxMessageEntity> findBySagaTypeAndOutboxStatusAndSagaStatusIn(
        String sagaType,
        String outboxStatus,
        String[] sagaStatuses) {

        String query = "SELECT * FROM restaurant_approval_outbox " +
            "WHERE saga_type = :sagaType " +
            "AND outbox_status = :outboxStatus " +
            "AND saga_status IN (:sagaStatuses)";

        return databaseClient.sql(query)
            .bind("sagaType", sagaType)
            .bind("outboxStatus", outboxStatus)
            .bind("sagaStatuses", Arrays.asList(sagaStatuses))  // Convert array to list
            .map(row -> {
                var entity = RestaurantApprovalOutboxMessageEntity.builder()
                    .id(row.get("id", UUID.class))
                    .sagaType(row.get("saga_type", String.class))
                    .outboxStatus(row.get("outbox_status", String.class))
                    .sagaStatus(row.get("saga_status", String.class))
                    .payload(row.get("payload", String.class))  // Assuming there's a payload column
                    .createdAt(row.get("created_at", ZonedDateTime.class))
                    .processedAt(row.get("processed_at", ZonedDateTime.class))
                    .build();
                return entity;
            })
            .all();  // Fetches all matching rows
    }

    @Override
    public Mono<RestaurantApprovalOutboxMessageEntity> findBySagaTypeAndOutboxStatus(
        String sagaType,
        String status) {

        String query = "SELECT * FROM restaurant_approval_outbox " +
            "WHERE saga_type = :sagaType AND outbox_status = :status " +
            "LIMIT 1";

        return databaseClient.sql(query)
            .bind("sagaType", sagaType)
            .bind("status", status)
            .map(row -> {
                var entity = RestaurantApprovalOutboxMessageEntity.builder()
                    .id(row.get("id", UUID.class))
                    .sagaType(row.get("saga_type", String.class))
                    .outboxStatus(row.get("outbox_status", String.class))
                    .sagaStatus(row.get("saga_status", String.class))
                    .payload(row.get("payload", String.class))  // Assuming there's a payload column
                    .createdAt(row.get("created_at", ZonedDateTime.class))
                    .processedAt(row.get("processed_at", ZonedDateTime.class))
                    .build();
                return entity;
            })
            .one();  // Fetches one matching row
    }
}
