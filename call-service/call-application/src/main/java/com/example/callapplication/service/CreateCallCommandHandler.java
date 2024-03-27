package com.example.callapplication.service;

import com.example.callapplication.data.dto.CallResponseDto;
import com.example.callapplication.service.listener.kafka.PaymentResponseKafkaListener;
import com.example.callapplication.service.publisher.kafka.PaymentRequestKafkaProducer;
import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.calldomain.data.mapper.DataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreateCallCommandHandler {

    private final CreateCallCommandManager createCallCommandManager;
    private final DataMapper dataMapper;
    private final PaymentRequestKafkaProducer requestPaymentKafkaPublisher;


    public CallResponseDto command(CreateCallCommandDto createCallCommandDto) {
        CallCreatedEvent callCreatedEvent = createCallCommandManager.createCallTransaction(createCallCommandDto);
        log.info("call saved. Id: {}", callCreatedEvent.getCall().getId().getValue());
        requestPaymentKafkaPublisher.publish(callCreatedEvent);
        var call = callCreatedEvent.getCall();
        return CallResponseDto.builder().callStatus(call.getCallStatus()).callTrackingId(call.getTrackingId().getValue()).build();
    }


}
