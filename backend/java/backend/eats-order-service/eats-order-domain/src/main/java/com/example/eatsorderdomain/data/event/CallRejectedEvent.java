package com.example.eatsorderdomain.data.event;

import com.example.eatsorderdomain.data.aggregate.OrderDomainObject;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;

import java.time.ZonedDateTime;

public class CallRejectedEvent extends AbstractCallEvent {
    DomainEventPublisher<CallCreatedEvent> domainEventPublisher;

    public CallRejectedEvent(OrderDomainObject orderDomainObject, ZonedDateTime time) {
        super(orderDomainObject, time);
    }

    @Override
    public void fire() {
    }
}
