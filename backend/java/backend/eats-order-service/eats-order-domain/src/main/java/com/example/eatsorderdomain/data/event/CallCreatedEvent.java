package com.example.eatsorderdomain.data.event;

import com.example.eatsorderdomain.data.domainentity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class CallCreatedEvent extends AbstractCallEvent {
//    DomainEventPublisher<CallCreatedEvent> domainEventPublisher;

    public CallCreatedEvent(Order orderDomainObject, ZonedDateTime createAt) {
        super(orderDomainObject, createAt);
    }

    @Override
    public void fire() {
//        domainEventPublisher.publish(this);
    }
}
