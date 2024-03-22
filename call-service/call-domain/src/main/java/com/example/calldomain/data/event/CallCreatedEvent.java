package com.example.calldomain.data.event;

import com.example.calldomain.data.aggregate.Call;
import com.example.commondata.domain.event.DomainEvent;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;

public class CallCreatedEvent extends AbstractCallEvent {
    DomainEventPublisher<CallCreatedEvent> domainEventPublisher;

    public CallCreatedEvent(Call call) {
        super(call);
    }

    @Override
    public void fire() {
        domainEventPublisher.publish(this);
    }
}
