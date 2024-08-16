package com.example.eatsorderdataaccess.entity;

import com.example.commondata.domain.aggregate.valueobject.ApprovalStatus;
import com.example.commondata.domain.aggregate.valueobject.OrderId;
import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.commondata.domain.aggregate.valueobject.SimpleId;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "order_approval")
public class OrderApprovalEntity {
    @NotNull
    @Id
    private UUID id;
    @NotNull
    @Column(name = "restaurant_id")
    private UUID restaurantId;
    @NotNull
    @Column(name = "order_id")
    private UUID orderId;
    @NotNull
    @Column(name = "status")
    private String status;
}
