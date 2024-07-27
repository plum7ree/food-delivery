package com.example.eatsorderdomain.data.domainentity;

import com.example.commondata.domain.aggregate.AggregateRoot;
import com.example.commondata.domain.aggregate.valueobject.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

//@Data //TODO hashCode conflict 에러 뜨는데 AggregateRoot 거 사용하기.
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Order extends AggregateRoot<OrderId> implements Cloneable { // why clonable?
    // private final: these are immutable.
    @JsonProperty
    private CallerId callerId;
    @JsonProperty
    private CalleeId calleeId;
    @JsonProperty
    private Money price;

    // private: these are mutable.
    @JsonProperty
    private SimpleId trackingId;
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