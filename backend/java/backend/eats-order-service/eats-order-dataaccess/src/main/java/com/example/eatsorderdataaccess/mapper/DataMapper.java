package com.example.eatsorderdataaccess.mapper;

import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdomain.data.aggregate.OrderDomainObject;
import com.example.commondata.domain.aggregate.valueobject.OrderId;
import com.example.commondata.domain.aggregate.valueobject.CalleeId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.CallerId;
import org.springframework.stereotype.Component;

@Component("callDataAccessMapper")
// Enttiy <-> Call (contains multiple ValueObject. CallId, DriverId, Money, ...)
public class DataMapper {

    public OrderEntity callToCallEntity(OrderDomainObject orderDomainObject) {
        // CallId extends BaseId, BaseId contains UUID. getValue().
        return OrderEntity.builder()
            .id(orderDomainObject.getId().getValue())
            .customerId(orderDomainObject.getCallerId().getValue())
            .restaurantId(orderDomainObject.getCalleeId().getValue())
            .price(orderDomainObject.getPrice().getAmount())
            .orderStatus(orderDomainObject.getOrderStatus())
                .build();
    }


    public OrderDomainObject callEntityToCall(OrderEntity orderEntity) {
        return OrderDomainObject.builder()
            .id(new OrderId(orderEntity.getId()))
            .callerId(new CallerId(orderEntity.getCustomerId()))
            .calleeId(new CalleeId(orderEntity.getRestaurantId()))
            .price(new Money(orderEntity.getPrice()))
            .orderStatus(orderEntity.getOrderStatus())
                .build();
    }
}
