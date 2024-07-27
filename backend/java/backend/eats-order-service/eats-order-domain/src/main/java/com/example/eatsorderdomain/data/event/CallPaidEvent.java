package com.example.eatsorderdomain.data.event;

import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;

import java.time.ZonedDateTime;

public class CallPaidEvent extends AbstractCallEvent {
    DomainEventPublisher<CallPaidEvent> domainEventPublisher;

    public CallPaidEvent(Order orderDomainObject, ZonedDateTime time, DomainEventPublisher<CallPaidEvent> domainEventPublisher) {
        super(orderDomainObject, time);
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public void fire() {
        domainEventPublisher.publish(this);
    }
}
