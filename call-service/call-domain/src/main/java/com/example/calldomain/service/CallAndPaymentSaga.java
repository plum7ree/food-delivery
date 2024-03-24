package com.example.calldomain.service;

import com.example.calldataaccess.repository.adapter.CallRepositoryAdapter;
import com.example.calldomain.data.event.CallPaidEvent;
import com.example.calldomain.data.event.EmptyEvent;
import com.example.kafka.avro.model.PaymentResponseAvroModel;
import com.example.commondata.domain.pattern.SagaStep;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class CallAndPaymentSaga implements SagaStep<PaymentResponseAvroModel, CallPaidEvent,EmptyEvent> {

    private final CallDomainService callDomainService;
    private final CallRepositoryAdapter callRepositoryAdapter;

    @Override
    @Transactional
    CallPaidEvent process(PaymentResponseAvroModel data) {
        var callFound = callRepositoryAdapter.findById(data.getCallId());
        var event = callDomainService.processCallPaid(callFound);
        return event;
    }

    @Override
    @Transactional
    EmptyEvent rollback(PaymentResponseAvroModel data) {

    }

}
