package com.example.eatsorderdataaccess.entity;

import jakarta.annotation.Nonnull;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

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

    //TODO json 와 jsonb 의 차이.
    @Nonnull
    @Column(name = "payload")
    @JdbcTypeCode(SqlTypes.JSON)
    @Setter
    private String payload;
    @Nonnull
    @Column(name = "order_status")
    private String orderStatus;
    @Nonnull
    @Column(name = "outbox_status")
    private String outboxStatus;
    @Nonnull
    @Column(name = "saga_status")
    private String sagaStatus;

    @Version
    private int version;
}

