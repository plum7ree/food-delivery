package com.example.callapplication.service;

import com.example.calldomain.data.aggregate.Call;
import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.calldomain.data.mapper.DataMapper;

public class CreateCallCommandManager {
    private final DataMapper dataMapper;

    public CreateCallCommandManager(DataMapper dataMapper) {
        this.dataMapper = dataMapper;
    }

    public CallCreatedEvent createOrderTransaction(CreateCallCommandDto createCallCommandDto) {
        Call call = dataMapper.createCallCommandDtoToCall(createCallCommandDto);
        return new CallCreatedEvent(call);
    }
}
