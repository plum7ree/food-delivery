package com.example.eatsorderapplication.service.publisher.kafka;

import com.example.commondata.domain.event.publisher.DomainEventPublisher;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdomain.data.event.CallPaidEvent;
import com.example.eatsorderdomain.data.mapper.DtoDataMapper;
import com.example.kafka.avro.model.DriverApprovalRequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentRequestKafkaProducer implements DomainEventPublisher<CallPaidEvent> {

    private final EatsOrderServiceConfigData eatsOrderServiceConfigData;
    private final KafkaProducer<String, DriverApprovalRequestAvroModel> kafkaProducer;

    @Override
    public void publish(CallPaidEvent domainEvent) {
        String callId = domainEvent.getOrderDomainObject().getId().getValue().toString();

        var RequestAvroModel = DtoDataMapper
            .callPaidEventToRequestAvroModel(domainEvent);

        kafkaProducer.send(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName(),
            callId,
            RequestAvroModel);


    }

}