package com.example.eatsorderdomain.data.event;

import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;

import java.time.ZonedDateTime;

public class CallPaidEvent extends AbstractCallEvent {
    DomainEventPublisher<CallPaidEvent> domainEventPublisher;

    public CallPaidEvent(Call call, ZonedDateTime time, DomainEventPublisher<CallPaidEvent> domainEventPublisher) {
        super(call, time);
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public void fire() {
        domainEventPublisher.publish(this);
    }
}
