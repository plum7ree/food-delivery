package com.example.eatsorderapplication.controller;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderapplication.service.EatsOrderCommandService;
import com.example.eatsorderdataaccess.repository.CallRepository;
import com.example.eatsorderdomain.data.dto.CreateEatsOrderCommandDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class EatesOrderController {
    private static final Logger log = LoggerFactory.getLogger(EatesOrderController.class);

    @Autowired
    private final EatsOrderCommandService eatsOrderCommandService;

    @Autowired
    private final CallRepository callRepository;

    @PostMapping("/eatsorder")
    public ResponseEntity<EatsOrderResponseDto> callDriver(@RequestBody CreateEatsOrderCommandDto createEatsOrderCommandDto) {
        try {
            var response = eatsOrderCommandService.createCall(createEatsOrderCommandDto);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            var response = EatsOrderResponseDto.builder().message("internal error occured").build();
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
