package com.example.eatsorderdataaccess.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_outbox")
@Entity
public class OrderOutboxEntity {
    @Id
    private UUID id;
    @Column(name = "correlation_id")
    UUID correlationId;
    String status;
}
