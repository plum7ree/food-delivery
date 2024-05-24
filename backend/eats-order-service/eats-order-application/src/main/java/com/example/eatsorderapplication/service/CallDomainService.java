package com.example.eatsorderapplication.service;

import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.eatsorderdomain.data.event.CallPaidEvent;
import com.example.eatsorderdomain.data.event.CallRejectedEvent;
import com.example.eatsorderdomain.data.event.EmptyEvent;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Component
public class CallDomainService {


    CallPaidEvent processCallPaid(Call call, DomainEventPublisher<CallPaidEvent> domainEventPublisher) {
        call.updateStatusToPaid();
        return new CallPaidEvent(call, ZonedDateTime.now(ZoneId.of("UTC")), domainEventPublisher);
    }

    public CallRejectedEvent processCallRejected(Call call) {
        call.updateStatusToRejected();
        return new CallRejectedEvent(call, ZonedDateTime.now(ZoneId.of("UTC")));
    }

    public EmptyEvent processCallApproved(Call call) {
        call.updateStatusToApproved();
        return new EmptyEvent();
    }
}
