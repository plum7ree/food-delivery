//package com.example.eatsorderapplication.service;
//
//import com.example.eatsorderapplication.service.publisher.kafka.PaymentRequestKafkaProducer;
//import com.example.eatsorderdomain.data.dto.ResponseDto;
//import com.example.eatsorderdomain.data.event.CallPaidEvent;
//import com.example.eatsorderdomain.data.event.EmptyEvent;
//import com.example.commondata.domain.aggregate.valueobject.OrderId;
//import com.example.commondata.domain.pattern.SagaStep;
//import lombok.RequiredArgsConstructor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.UUID;
//
//@Component
//@RequiredArgsConstructor
//public class CallAndPaymentSaga implements SagaStep<ResponseDto, CallPaidEvent, EmptyEvent> {
//    private static final Logger log = LoggerFactory.getLogger(CallAndPaymentSaga.class);
//
//    private final CallDomainService callDomainService;
//    private final PaymentRequestKafkaProducer paymentRequestKafkaProducer;
//
//    @Override
//    @Transactional
//    public CallPaidEvent process(ResponseDto data) {
//        var callId = new OrderId(UUID.fromString(data.getCallId()));
//        var callFound = orderRepositoryAdaptor.findById(callId);
//        if (callFound.isEmpty()) {
//            log.error("Order with id: {} could not be found!", data.getCallId());
//            throw new RuntimeException("Call with id " + data.getCallId() + " could not be found!");
//        }
//        var event = callDomainService.processCallPaid(callFound.get(), paymentRequestKafkaProducer);
//        return event;
//    }
//
//    @Override
//    @Transactional
//    public EmptyEvent rollback(ResponseDto data) {
//        return new EmptyEvent();
//    }
//
//}
