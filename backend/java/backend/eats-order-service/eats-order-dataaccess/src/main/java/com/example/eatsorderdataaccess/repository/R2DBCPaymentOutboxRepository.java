package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.PaymentOutboxMessageEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.time.ZonedDateTime;
import java.util.UUID;

@Repository
public class R2DBCPaymentOutboxRepository implements PaymentOutboxRepository {

    private final DatabaseClient databaseClient;

    public R2DBCPaymentOutboxRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<PaymentOutboxMessageEntity> upsert(PaymentOutboxMessageEntity entity) {

        String query = "INSERT INTO payment_outbox (id, saga_id, saga_type, payload, order_status, outbox_status, saga_status, created_at, processed_at) " +
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
    public Mono<PaymentOutboxMessageEntity> findBySagaIdAndSagaStatus(String sagaId, String sagaStatus) {
        String query = "SELECT * FROM payment_outbox " +
            "WHERE saga_id = :sagaId AND saga_status = :sagaStatus";

        return databaseClient.sql(query)
            .bind("sagaId", sagaId)
            .bind("sagaStatus", sagaStatus)
            .map(row -> {
                var entity = PaymentOutboxMessageEntity.builder()
                    .id(row.get("id", UUID.class))
                    .sagaId(row.get("saga_id", UUID.class))
                    .createdAt(row.get("created_at", ZonedDateTime.class))
                    .processedAt(row.get("processed_at", ZonedDateTime.class))
                    .sagaType(row.get("saga_type", String.class))
                    .payload(row.get("payload", String.class))
                    .orderStatus(row.get("order_status", String.class))
                    .outboxStatus(row.get("outbox_status", String.class))
                    .sagaStatus(row.get("saga_status", String.class))
                    .version(row.get("version", Integer.class))
                    .build();
                return entity;
            })
            .one();
    }
}
