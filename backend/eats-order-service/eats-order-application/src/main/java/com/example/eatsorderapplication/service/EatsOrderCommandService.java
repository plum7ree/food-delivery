package com.example.eatsorderapplication.service;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderdomain.data.dto.CreateEatsOrderCommandDto;
import org.springframework.stereotype.Service;

@Service
public class EatsOrderCommandService {

    private final CreateCallCommandHandler createCallCommandHandler;

    public EatsOrderCommandService(CreateCallCommandHandler createCallCommandHandler) {
        this.createCallCommandHandler = createCallCommandHandler;
    }

    public EatsOrderResponseDto createCall(CreateEatsOrderCommandDto createEatsOrderCommandDto) {
        return createCallCommandHandler.command(createEatsOrderCommandDto);
    }
}
