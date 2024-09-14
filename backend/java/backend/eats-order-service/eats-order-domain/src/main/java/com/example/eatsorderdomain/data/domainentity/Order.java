package com.example.eatsorderdomain.data.domainentity;

import com.example.commondata.domain.aggregate.valueobject.Address;
import com.example.commondata.domain.events.order.OrderStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

//@Data //TODO hashCode conflict 에러 뜨는데 AggregateRoot 거 사용하기.
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Order {
    @JsonProperty
    private UUID id;
    @JsonProperty
    private UUID callerId;
    @JsonProperty
    private UUID calleeId;
    @JsonProperty
    private Double price;

    // private: these are mutable.
    @JsonProperty
    private OrderStatus orderStatus;
    @JsonProperty
    private Address address;

    @JsonProperty
    private List<OrderItem> items;
    @JsonProperty
    private List<String> failureMessages;

    @JsonProperty
    private List<Coupon> coupons;

}