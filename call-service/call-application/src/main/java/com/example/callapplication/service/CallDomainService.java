package com.example.callapplication.service;

import com.example.calldomain.data.aggregate.Call;
import com.example.calldomain.data.event.CallPaidEvent;
import com.example.calldomain.data.event.CallRejectedEvent;
import com.example.calldomain.data.event.EmptyEvent;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class CallDomainService {


    CallPaidEvent processCallPaid(Call call, DomainEventPublisher<CallPaidEvent> domainEventPublisher) {
        call.updateStateToPaid();
        return new CallPaidEvent(call, domainEventPublisher);
    }

    public CallRejectedEvent processCallRejected(Call call) {
        call.updateStateToRejected();
        return new CallRejectedEvent(call);
    }

    public EmptyEvent processCallApproved(Call call) {
        call.updateStateToApproved();
        return new EmptyEvent();
    }
}
