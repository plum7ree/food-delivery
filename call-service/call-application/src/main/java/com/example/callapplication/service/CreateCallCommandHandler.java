package com.example.callapplication.service;

import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.calldomain.data.mapper.DataMapper;
import com.example.callapplication.service.publisher.kafka.PaymentRequestKafkaProducer;
import org.springframework.stereotype.Component;

@Component
public class CreateCallCommandHandler {

    CreateCallCommandManager createCallCommandManager;
    DataMapper dataMapper;
    private final PaymentRequestKafkaProducer requestPaymentKafkaPublisher;

    public CreateCallCommandHandler(PaymentRequestKafkaProducer requestPaymentKafkaPublisher) {
        this.requestPaymentKafkaPublisher = requestPaymentKafkaPublisher;
    }

    public void command(CreateCallCommandDto createCallCommandDto) {
        CallCreatedEvent callCreatedEvent = createCallCommandManager.createOrderTransaction(createCallCommandDto);
        requestPaymentKafkaPublisher.publish(callCreatedEvent);

    }


}
