package com.example.calldomain.data.event;

import com.example.calldomain.data.aggregate.Call;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;

import java.time.ZonedDateTime;

public class CallRejectedEvent extends AbstractCallEvent {
    DomainEventPublisher<CallCreatedEvent> domainEventPublisher;

    public CallRejectedEvent(Call call, ZonedDateTime time) {
        super(call, time);
    }

    @Override
    public void fire() {
    }
}
