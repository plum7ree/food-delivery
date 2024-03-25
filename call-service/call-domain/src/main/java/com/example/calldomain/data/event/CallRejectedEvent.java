package com.example.calldomain.data.event;

import com.example.calldomain.data.aggregate.Call;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;

public class CallRejectedEvent extends AbstractCallEvent {
    DomainEventPublisher<CallCreatedEvent> domainEventPublisher;

    public CallRejectedEvent(Call call) {
        super(call);
    }

    @Override
    public void fire() {
    }
}
