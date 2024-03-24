
package com.example.calldomain.data.event;

import com.example.calldomain.data.aggregate.Call;
import com.example.commondata.domain.event.DomainEvent;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;

public class CallPaidEvent extends AbstractCallEvent {
    DomainEventPublisher<CallPaidEvent> domainEventPublisher;

    public CallPaidEvent(Call call, DomainEventPublisher<CallPaidEvent> domainEventPublisher) {
        super(call);
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public void fire() {
        domainEventPublisher.publish(this);
    }
}
