package com.example.calldomain.service;

import com.example.calldomain.data.dto.CreateCallCommandDto;

public class CallCommandService {

    private final CreateCallCommandHandler createCallCommandHandler;

    public CallCommandService(CreateCallCommandHandler createCallCommandHandler) {
        this.createCallCommandHandler = createCallCommandHandler;
    }

    public void createCall(CreateCallCommandDto createCallCommandDto) {
        createCallCommandHandler.command(createCallCommandDto);
    }
}
