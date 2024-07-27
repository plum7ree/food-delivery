package com.example.eatsorderdataaccess.entity;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;

import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * OrderDomainObject 을 감싸는 엔티티 클래스
 */

@Entity
@Table(name = "restaurant_approval_outbox")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RestaurantApprovalOutboxMessageEntity extends BaseOutboxMessageEntity {

}

