package com.example.callapplication.controller;

import com.example.callapplication.data.dto.CallResponseDto;
import com.example.callapplication.service.CallCommandService;
import com.example.calldataaccess.repository.CallRepository;
import com.example.calldomain.data.dto.CreateCallCommandDto;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Call;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class CallController {
    private static final Logger log = LoggerFactory.getLogger(CallController.class);

    @Autowired
    private final CallCommandService callCommandService;

    @Autowired
    private final CallRepository callRepository;

    @PostMapping("/call")
    public ResponseEntity<CallResponseDto> callDriver(@RequestBody CreateCallCommandDto createCallCommandDto) {
        try {
            var response = callCommandService.createCall(createCallCommandDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            var response = CallResponseDto.builder().message("internal error occured").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

//    @GetMapping("/fetch")
//    public ResponseEntity<Call> fetchMostRecentCallStatus(@RequestParam UUID userId) {
//        var callEntityList = callRepository.findCallEntitiesByUserId(userId, 1);
//
//        return ResponseEntity.ok(callEntityList.get(0));
//    }


}
