package com.example.eatsorderapplication.service;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdomain.data.dto.CreateEatsOrderCommandDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.eatsorderdomain.data.mapper.DataMapper;
import com.example.kafka.avro.model.RequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EatsOrderCommandService {

    private final CreateCallCommandManager createCallCommandManager;
//    private final DataMapper dataMapper;
    private final EatsOrderServiceConfigData eatsOrderServiceConfigData;
    private final KafkaProducer<String, RequestAvroModel> kafkaProducer;

    public EatsOrderResponseDto createAndPublishOrder(CreateEatsOrderCommandDto createEatsOrderCommandDto) {
        CallCreatedEvent callCreatedEvent = createCallCommandManager.createCallTransaction(createEatsOrderCommandDto);
        log.info("call saved. Id: {}", callCreatedEvent.getCall().getId().getValue());
        String callIdAsKey = callCreatedEvent.getCall().getId().getValue().toString();
        var call = callCreatedEvent.getCall();
        RequestAvroModel restaurantApprovalRequestAvroModel = DataMapper
                .callCreatedEventToRestaurantApprovalRequestAvroModel(callCreatedEvent);

        publish(callIdAsKey, restaurantApprovalRequestAvroModel);
        return EatsOrderResponseDto.builder().callStatus(call.getCallStatus()).callTrackingId(call.getTrackingId().getValue()).build();
    }


    private void publish(String key, RequestAvroModel restaurantApprovalRequestAvroModel) {

        log.info("kafka producer send. topic name: {} key: {}", eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName(), key);
        kafkaProducer.send(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                key,
                restaurantApprovalRequestAvroModel);


    }

}
