package com.example.callapplication.service.publisher.kafka;

import com.example.callconfigdata.CallServiceConfigData;
import com.example.calldomain.data.event.CallPaidEvent;
import com.example.calldomain.data.mapper.DataMapper;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;
import com.example.kafka.avro.model.DriverApprovalRequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;




@Component
@RequiredArgsConstructor
public class DriverApprovalRequestKafkaProducer implements DomainEventPublisher<CallPaidEvent> {
    private static final Logger log = LoggerFactory.getLogger(DriverApprovalRequestKafkaProducer.class);

    private final DataMapper callMessagingDataMapper;
    private final CallServiceConfigData callServiceConfigData;
    private final KafkaProducer<String, DriverApprovalRequestAvroModel> kafkaProducer;

    @Override
    public void publish(CallPaidEvent domainEvent) {
        String orderId = domainEvent.getCall().getId().getValue().toString();

        var driverApprovalRequestAvroModel = callMessagingDataMapper
                .callPaidEventToDriverApprovalRequestAvroModel(domainEvent);

        kafkaProducer.send(callServiceConfigData.getPaymentRequestTopicName(),
                orderId,
                driverApprovalRequestAvroModel);


    }

}