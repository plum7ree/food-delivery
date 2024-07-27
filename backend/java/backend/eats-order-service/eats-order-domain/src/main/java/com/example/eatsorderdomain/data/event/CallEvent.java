package com.example.eatsorderdomain.data.event;

import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.commondata.domain.event.DomainEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
abstract class AbstractCallEvent implements DomainEvent<Order> {
    private final Order orderDomainObject;
    private final ZonedDateTime createdAt;

}
