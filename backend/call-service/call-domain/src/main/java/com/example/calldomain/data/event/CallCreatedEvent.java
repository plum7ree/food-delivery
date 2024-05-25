package com.example.calldomain.data.event;

import com.example.calldomain.data.aggregate.Call;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class CallCreatedEvent extends AbstractCallEvent {
//    DomainEventPublisher<CallCreatedEvent> domainEventPublisher;

    public CallCreatedEvent(Call call, ZonedDateTime createAt) {
        super(call, createAt);
    }

    @Override
    public void fire() {
//        domainEventPublisher.publish(this);
    }
}
