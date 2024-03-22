package com.example.calldomain.service;

import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.calldomain.data.mapper.CallDataMapper;
import com.example.calldomain.service.publisher.kafka.PaymentRequestKafkaProducer;

public class CreateCallCommandHandler {

    CreateCallCommandManager createCallCommandManager;
    CallDataMapper callDataMapper;
    private final PaymentRequestKafkaProducer requestPaymentKafkaPublisher;

    public CreateCallCommandHandler(PaymentRequestKafkaProducer requestPaymentKafkaPublisher) {
        this.requestPaymentKafkaPublisher = requestPaymentKafkaPublisher;
    }

    public void command(CreateCallCommandDto createCallCommandDto) {
        CallCreatedEvent callCreatedEvent = createCallCommandManager.createOrderTransaction(createCallCommandDto);
        requestPaymentKafkaPublisher.publish(callCreatedEvent);

    }


}
