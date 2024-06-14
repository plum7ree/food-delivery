package com.example.eatsorderdataaccess.entity;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class OrderEntity {


    @Id
    @NotNull
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    private UUID customerId;
    private UUID restaurantId;
    private UUID trackingId;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "order_status")
    private OrderStatus orderStatus;
    private String failureMessages;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

