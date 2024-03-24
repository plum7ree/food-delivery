package com.example.calldomain.service;

import com.example.calldomain.data.aggregate.Call;
import com.example.calldomain.data.event.CallPaidEvent;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;

public class CallDomainService {


    CallPaidEvent processCallPaid(Call call, DomainEventPublisher<CallPaidEvent> domainEventPublisher) {
        call.updateStateToPaid();
        return new CallPaidEvent(call, domainEventPublisher);
    }
}
