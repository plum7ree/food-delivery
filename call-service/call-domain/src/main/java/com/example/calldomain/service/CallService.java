package com.example.calldomain.service;

import com.example.calldomain.data.dto.CreateCallCommandDto;

public class CallService {

    private final CreateCallCommandHandler createCallCommandHandler;

    public CallService(CreateCallCommandHandler createCallCommandHandler) {
        this.createCallCommandHandler = createCallCommandHandler;
    }

    public void createCall(CreateCallCommandDto createCallCommandDto) {
        createCallCommandHandler.command(createCallCommandDto);
    }
}
