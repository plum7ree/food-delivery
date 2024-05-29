package com.example.eatsorderapplication.service;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderapplication.service.publisher.kafka.RestaurantApprovalRequestKafkaProducer;
import com.example.eatsorderdomain.data.dto.CreateEatsOrderCommandDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.eatsorderdomain.data.mapper.DataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCallCommandHandler {

    private final CreateCallCommandManager createCallCommandManager;
//    private final DataMapper dataMapper;
    private final RestaurantApprovalRequestKafkaProducer requestPaymentKafkaPublisher;


    public EatsOrderResponseDto command(CreateEatsOrderCommandDto createEatsOrderCommandDto) {
        CallCreatedEvent callCreatedEvent = createCallCommandManager.createCallTransaction(createEatsOrderCommandDto);
        log.info("call saved. Id: {}", callCreatedEvent.getCall().getId().getValue());
        requestPaymentKafkaPublisher.publish(callCreatedEvent);
        var call = callCreatedEvent.getCall();
        return EatsOrderResponseDto.builder().callStatus(call.getCallStatus()).callTrackingId(call.getTrackingId().getValue()).build();
    }


}
