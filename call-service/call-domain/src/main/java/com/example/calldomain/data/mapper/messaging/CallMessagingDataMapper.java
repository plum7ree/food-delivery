package com.example.calldomain.data.mapper.messaging;

import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.kafka.avro.model.PaymentRequestAvroModel;

public class CallMessagingDataMapper {
    public PaymentRequestAvroModel callCreatedEventToPaymentRequestAvroModel(CallCreatedEvent domainEvent) {
        return PaymentRequestAvroModel.newBuilder()
                .build();
    }
}
