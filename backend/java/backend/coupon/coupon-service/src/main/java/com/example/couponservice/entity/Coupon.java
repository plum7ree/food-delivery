package com.example.couponservice.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupons")
public class Coupon {

    @Id
    private Long id;
    private Long eventId;
    private String discountType;
    private Long discountRate;
    private Long discountPrice;
    private String couponType;
    private Long maxQuantity;
    private Long issuedQuantity;
    private LocalDateTime validateStartDate;
    private LocalDateTime validateEndDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

// LocalDateTime -> DATETIME
// ZonedDateTime -> TIMESTAMP WITH TIME ZONE

}
