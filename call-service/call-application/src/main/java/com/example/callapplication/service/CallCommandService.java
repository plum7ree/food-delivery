package com.example.callapplication.service;

import com.example.callapplication.data.dto.CallResponseDto;
import com.example.calldomain.data.dto.CreateCallCommandDto;
import org.springframework.stereotype.Service;

@Service
public class CallCommandService {

    private final CreateCallCommandHandler createCallCommandHandler;

    public CallCommandService(CreateCallCommandHandler createCallCommandHandler) {
        this.createCallCommandHandler = createCallCommandHandler;
    }

    public CallResponseDto createCall(CreateCallCommandDto createCallCommandDto) {
        return createCallCommandHandler.command(createCallCommandDto);
    }
}
