package com.example.callapplication.controller;

import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.callapplication.service.CallCommandService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private final CallCommandService callCommandService;
    public void callDriver(@RequestParam CreateCallCommandDto createCallCommandDto) {
        callCommandService.createCall(createCallCommandDto);
    }

}
