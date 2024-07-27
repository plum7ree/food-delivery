package com.example.eatsorderdomain.data.domainentity;


import com.example.commondata.domain.aggregate.entity.BaseEntity;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.OrderId;
import com.example.commondata.domain.aggregate.valueobject.SimpleId;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class OrderItem extends BaseEntity<SimpleId> {
    @NotNull
    private OrderId orderId;
    @NotNull
    private SimpleId productId;
    @NotNull
    private Integer quantity;
    @NotNull
    private Money price;
    @NotNull
    private Money subTotal;


}
