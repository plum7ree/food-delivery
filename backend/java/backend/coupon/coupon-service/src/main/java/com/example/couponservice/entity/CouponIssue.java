package com.example.couponservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
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
