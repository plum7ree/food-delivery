package com.example.eatsorderdataaccess.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "payment_outbox")
@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PaymentOutboxMessageEntity extends BaseOutboxMessageEntity {
}