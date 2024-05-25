package com.example.eatsorderapplication.service.publisher.kafka;

import com.example.eatsorderconfigdata.CallServiceConfigData;
import com.example.eatsorderdomain.data.event.CallPaidEvent;
import com.example.eatsorderdomain.data.mapper.DataMapper;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;
import com.example.kafka.avro.model.DriverApprovalRequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestKafkaProducer implements DomainEventPublisher<CallPaidEvent> {

    private final DataMapper dataMapper;
    private final CallServiceConfigData callServiceConfigData;
    private final KafkaProducer<String, DriverApprovalRequestAvroModel> kafkaProducer;

    @Override
    public void publish(CallPaidEvent domainEvent) {
        String callId = domainEvent.getCall().getId().getValue().toString();

        var RequestAvroModel = dataMapper
                .callPaidEventToRequestAvroModel(domainEvent);

        kafkaProducer.send(callServiceConfigData.getDriverApprovalRequestTopicName(),
                callId,
                RequestAvroModel);


    }

}