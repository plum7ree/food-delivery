package com.example.callapplication.service.publisher.kafka;

import com.example.callconfigdata.CallServiceConfigData;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.calldomain.data.mapper.DataMapper;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;
import com.example.kafka.avro.model.PaymentRequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class PaymentRequestKafkaProducer implements DomainEventPublisher<CallCreatedEvent> {
    private static final Logger log = LoggerFactory.getLogger(PaymentRequestKafkaProducer.class);

    private final DataMapper callMessagingDataMapper;
    private final CallServiceConfigData callServiceConfigData;
    private final KafkaProducer<String, PaymentRequestAvroModel> kafkaProducer;

    @Override
    public void publish(CallCreatedEvent domainEvent) {
        String callId = domainEvent.getCall().getId().getValue().toString();

        PaymentRequestAvroModel paymentRequestAvroModel = callMessagingDataMapper
                .callCreatedEventToPaymentRequestAvroModel(domainEvent);
        log.info("kafka producer send. topic name: {} key: {}", callServiceConfigData.getPaymentRequestTopicName(), callId);
        kafkaProducer.send(callServiceConfigData.getPaymentRequestTopicName(),
                callId,
                paymentRequestAvroModel);


    }
}