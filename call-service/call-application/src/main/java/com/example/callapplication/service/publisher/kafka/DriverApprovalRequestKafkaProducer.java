package com.example.callapplication.service.publisher.kafka;

import com.example.callconfigdata.CallServiceConfigData;
import com.example.calldomain.data.event.CallPaidEvent;
import com.example.calldomain.data.mapper.DataMapper;
import com.example.commondata.domain.event.publisher.DomainEventPublisher;
import com.example.kafka.avro.model.DriverApprovalRequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class DriverApprovalRequestKafkaProducer implements DomainEventPublisher<CallPaidEvent> {

    private final DataMapper dataMapper;
    private final CallServiceConfigData callServiceConfigData;
    private final KafkaProducer<String, DriverApprovalRequestAvroModel> kafkaProducer;

    @Override
    public void publish(CallPaidEvent domainEvent) {
        String callId = domainEvent.getCall().getId().getValue().toString();

        var driverApprovalRequestAvroModel = dataMapper
                .callPaidEventToDriverApprovalRequestAvroModel(domainEvent);

        kafkaProducer.send(callServiceConfigData.getDriverApprovalRequestTopicName(),
                callId,
                driverApprovalRequestAvroModel);


    }

}