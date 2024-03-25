package com.example.callapplication.service;

import com.example.calldataaccess.repository.adapter.CallRepositoryAdapter;
import com.example.calldomain.data.dto.PaymentResponseDto;
import com.example.calldomain.data.event.CallPaidEvent;
import com.example.calldomain.data.event.EmptyEvent;
import com.example.callapplication.service.publisher.kafka.DriverApprovalRequestKafkaProducer;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.pattern.SagaStep;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CallAndPaymentSaga implements SagaStep<PaymentResponseDto, CallPaidEvent,EmptyEvent> {

    private final CallDomainService callDomainService;
    private final CallRepositoryAdapter callRepositoryAdapter;
    private final DriverApprovalRequestKafkaProducer driverApprovalRequestKafkaProducer;

    @Override
    @Transactional
    public CallPaidEvent process(PaymentResponseDto data) {
        var callId = new CallId(UUID.fromString(data.getId()));
        var callFound = callRepositoryAdapter.findById(callId);
        var event = callDomainService.processCallPaid(callFound.get(), driverApprovalRequestKafkaProducer);
        return event;
    }

    @Override
    @Transactional
    public EmptyEvent rollback(PaymentResponseDto data) {
        return new EmptyEvent();
    }

}
