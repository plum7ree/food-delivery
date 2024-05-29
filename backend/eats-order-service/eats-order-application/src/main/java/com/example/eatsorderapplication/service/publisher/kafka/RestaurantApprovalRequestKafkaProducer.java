package com.example.eatsorderapplication.service.publisher.kafka;

import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.eatsorderdomain.data.mapper.DataMapper;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;
import com.example.kafka.avro.model.RequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class RestaurantApprovalRequestKafkaProducer implements DomainEventPublisher<CallCreatedEvent> {
    private static final Logger log = LoggerFactory.getLogger(RestaurantApprovalRequestKafkaProducer.class);

    private final DataMapper callMessagingDataMapper;
    private final EatsOrderServiceConfigData eatsOrderServiceConfigData;
    private final KafkaProducer<String, RequestAvroModel> kafkaProducer;

    @Override
    public void publish(CallCreatedEvent domainEvent) {
        String callId = domainEvent.getCall().getId().getValue().toString();

        RequestAvroModel restaurantApprovalRequestAvroModel = callMessagingDataMapper
                .callCreatedEventToRestaurantApprovalRequestAvroModel(domainEvent);
        log.info("kafka producer send. topic name: {} key: {}", eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName(), callId);
        kafkaProducer.send(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                callId,
                restaurantApprovalRequestAvroModel);


    }
}