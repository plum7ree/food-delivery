package com.example.eatsorderdataaccess.entity;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;

import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant_approval_outbox")
@Entity
public class RestaurantApprovalRequestEntity {

    @Id
    private UUID id;
    private UUID sagaId;
    private ZonedDateTime createdAt;
    private ZonedDateTime processedAt;
    private String type;
    private String payload;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private OutboxStatus outboxStatus;

    @Enumerated(EnumType.STRING)
    private SagaStatus sagaStatus;

    @Version
    private int version;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantApprovalRequestEntity that = (RestaurantApprovalRequestEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
