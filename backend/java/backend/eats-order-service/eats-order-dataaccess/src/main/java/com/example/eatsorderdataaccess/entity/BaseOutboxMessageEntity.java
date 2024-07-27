package com.example.eatsorderdataaccess.entity;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import jakarta.annotation.Nonnull;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@MappedSuperclass
@EqualsAndHashCode(of = "id")
public abstract class BaseOutboxMessageEntity {

    @Nonnull // compile check for null
    @Id
    private UUID id;
    @Nonnull
    @Column(name = "saga_id")
    private UUID sagaId;
    @Nonnull
    @Column(name = "created_at")
    private ZonedDateTime createdAt;
    @Nonnull
    @Column(name = "processed_at")
    private ZonedDateTime processedAt;
    @Nonnull
    @Column(name = "saga_type")
    private String sagaType;
    @Nonnull
    @Column(name = "payload")
    private String payload;
    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(name = "order_status", columnDefinition = "order_status")
    private OrderStatus orderStatus;
    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(name = "outbox_status", columnDefinition = "outbox_status")
    private OutboxStatus outboxStatus;
    @Nonnull
    @Enumerated(EnumType.STRING)
    @Column(name = "saga_status", columnDefinition = "saga_status")
    private SagaStatus sagaStatus;

    @Version
    private int version;
}

