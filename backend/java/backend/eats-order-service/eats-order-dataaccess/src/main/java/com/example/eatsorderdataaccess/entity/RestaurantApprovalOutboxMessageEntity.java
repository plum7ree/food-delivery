package com.example.eatsorderdataaccess.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

/**
 * OrderDomainObject 을 감싸는 엔티티 클래스
 */

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurant_approval_outbox")
@Entity
public class RestaurantApprovalOutboxMessageEntity {
    @Id
    private UUID id;
    @Column(name = "correlation_id")
    UUID correlationId;
    String status;
}

