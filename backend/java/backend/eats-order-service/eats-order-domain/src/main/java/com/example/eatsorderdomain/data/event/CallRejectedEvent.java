package com.example.eatsorderdomain.data.event;

import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;

import java.time.ZonedDateTime;

public class CallRejectedEvent extends AbstractCallEvent {
    DomainEventPublisher<CallCreatedEvent> domainEventPublisher;

    public CallRejectedEvent(Order orderDomainObject, ZonedDateTime time) {
        super(orderDomainObject, time);
    }

    @Override
    public void fire() {
    }
}
