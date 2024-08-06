package com.example.eatsorderapplication.controller;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderapplication.service.EatsOrderCommandService;
import com.example.eatsorderdomain.data.dto.CreateOrderCommandDto;
import com.example.eatsorderdomain.data.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class EatsOrderController {
    private static final Logger log = LoggerFactory.getLogger(EatsOrderController.class);

    @Autowired
    private final EatsOrderCommandService eatsOrderCommandService;

    private final RestTemplate restTemplate;

    @Value("${user.service.url}")
    private String userServiceUrl;


    /**
     * @param headers
     * @param createOrderCommandDto
     * @return
     */
    @PostMapping("/createorder")
    public ResponseEntity<EatsOrderResponseDto> createOrder(@RequestHeader HttpHeaders headers, @RequestBody CreateOrderCommandDto createOrderCommandDto) {
        try {

            var response = eatsOrderCommandService.createAndSaveOrder(createOrderCommandDto);
            return ResponseEntity.ok(response);
//            return ResponseEntity.ok(null);
        } catch (Exception e) {
            log.error(e.toString());
            var response = EatsOrderResponseDto.builder().message("internal error occured").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/cancelorder")
    public ResponseEntity<EatsOrderResponseDto> cancelOrder(@RequestHeader HttpHeaders headers, @RequestBody CreateOrderCommandDto createOrderCommandDto) {
        try {

            var response = eatsOrderCommandService.createAndSaveOrder(createOrderCommandDto);
            return ResponseEntity.ok(response);
//            return ResponseEntity.ok(null);
        } catch (Exception e) {
            var response = EatsOrderResponseDto.builder().message("internal error occured").build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    @PostMapping("/test")
    public ResponseEntity<String> testPost() {

        return ResponseEntity.ok("done");


    }
//    @GetMapping("/fetch")
//    public ResponseEntity<Call> fetchMostRecentCallStatus(@RequestParam UUID userId) {
//        var callEntityList = callRepository.findCallEntitiesByUserId(userId, 1);
//
//        return ResponseEntity.ok(callEntityList.get(0));
//    }


}
