package com.example.eatsorderdataaccess.entity;

import com.example.commondata.domain.aggregate.valueobject.OrderId;
import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.commondata.domain.aggregate.valueobject.SimpleId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderApprovalEntity {
    @Id
    private UUID id;
    private UUID restaurantId;
    private UUID orderId;
    private RestaurantApprovalStatus status;
}
