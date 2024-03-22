package com.example.calldomain.data.mapper;

import com.example.calldomain.data.aggregate.Call;
import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.kafka.avro.model.PaymentRequestAvroModel;

public class CallDataMapper {
    public Call createCallCommandDtoToCall(CreateCallCommandDto createCallCommandDto) {
        return Call.builder().build();
    }
}
