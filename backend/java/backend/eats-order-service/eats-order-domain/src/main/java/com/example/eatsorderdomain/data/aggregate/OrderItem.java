package com.example.eatsorderdomain.data.aggregate;


import com.example.commondata.domain.aggregate.entity.BaseEntity;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.OrderId;
import com.example.commondata.domain.aggregate.valueobject.SimpleId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class OrderItem extends BaseEntity<SimpleId> {
    private OrderId orderId;
    private Product product;
    private Integer quantity;
    private Money price;
    private Money subTotal;


}
