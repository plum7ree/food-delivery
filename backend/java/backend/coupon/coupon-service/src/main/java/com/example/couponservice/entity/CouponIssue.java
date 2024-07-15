package com.example.couponservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CouponIssue {

    @Id
    private Long id;
    private UUID memberId;
    private Long couponId;

    private String couponStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean checkRelatedIssuedQuantity;

}
