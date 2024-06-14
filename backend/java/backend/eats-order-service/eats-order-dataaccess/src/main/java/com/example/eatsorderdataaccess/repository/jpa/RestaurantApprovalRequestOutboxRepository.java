package com.example.eatsorderdataaccess.repository.jpa;


import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalRequestEntity;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Repository
public interface RestaurantApprovalRequestOutboxRepository extends JpaRepository<RestaurantApprovalRequestEntity, UUID> {

    @Modifying
    @Query(value = "INSERT INTO restaurant_approval_outbox (id, saga_id, created_at, processed_at, type, payload, order_status, outbox_status, version) " +
        "VALUES (:id, :sagaId, :createdAt, :processedAt, :type, :payload, cast(:orderStatus as order_status), cast(:outboxStatus as outbox_status), :version)",
        nativeQuery = true)
    void saveWithCast(@Param("id") UUID id,
                      @Param("sagaId") UUID sagaId,
                      @Param("createdAt") ZonedDateTime createdAt,
                      @Param("processedAt") ZonedDateTime processedAt,
                      @Param("type") String type,
                      @Param("payload") String payload,
                      @Param("orderStatus") OrderStatus orderStatus,
                      @Param("outboxStatus") OutboxStatus outboxStatus,
                      @Param("version") int version);
}
