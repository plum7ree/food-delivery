//package com.example.eatsorderapplication.service;
//
//import com.example.eatsorderapplication.service.publisher.kafka.PaymentRequestKafkaProducer;
//import com.example.eatsorderdomain.data.dto.ResponseDto;
//import com.example.eatsorderdomain.data.event.CallRejectedEvent;
//import com.example.eatsorderdomain.data.event.EmptyEvent;
//import com.example.commondata.domain.aggregate.valueobject.OrderId;
//import com.example.commondata.domain.pattern.SagaStep;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class RestaurantAndPaymentSaga implements SagaStep<ResponseDto, EmptyEvent, CallRejectedEvent> {
//
//    private final CallDomainService callDomainService;
//    private final PaymentRequestKafkaProducer paymentRequestKafkaProducer;
//
//    @Override
//    @Transactional
//    public EmptyEvent process(ResponseDto data) {
//        var callId = new OrderId(UUID.fromString(data.getCallId()));
//        var callFound = orderRepositoryAdaptor.findById(callId);
//        if (callFound.isEmpty()) {
//            throw new RuntimeException("Call with id " + data.getCallId() + " could not be found!");
//        }
//        var event = callDomainService.processCallApproved(callFound.get());
//        return event;
//    }
//
//    @Override
//    @Transactional
//    public CallRejectedEvent rollback(ResponseDto data) {
//        var callId = new OrderId(UUID.fromString(data.getId()));
//        var callFound = orderRepositoryAdaptor.findById(callId);
//        var event = callDomainService.processCallRejected(callFound.get());
//        return event;
//    }
//
//
//}
