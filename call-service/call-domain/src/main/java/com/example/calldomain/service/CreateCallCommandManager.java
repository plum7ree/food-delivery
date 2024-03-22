package com.example.calldomain.service;

import com.example.calldomain.data.aggregate.Call;
import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.calldomain.data.mapper.CallDataMapper;

public class CreateCallCommandManager {
    private final CallDataMapper callDataMapper;

    public CreateCallCommandManager(CallDataMapper callDataMapper) {
        this.callDataMapper = callDataMapper;
    }

    public CallCreatedEvent createOrderTransaction(CreateCallCommandDto createCallCommandDto) {
        Call call = callDataMapper.createCallCommandDtoToCall(createCallCommandDto);
        return new CallCreatedEvent(call);
    }
}
