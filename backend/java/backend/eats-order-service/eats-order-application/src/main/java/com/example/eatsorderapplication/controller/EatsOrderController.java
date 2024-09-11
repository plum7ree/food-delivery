package com.example.eatsorderapplication.controller;

import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderapplication.service.OrderService;
import com.example.eatsorderdomain.data.dto.CreateOrderRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class EatsOrderController {

    @Autowired
    private final OrderService orderService;


    @PostMapping("/createorder")
    public Mono<ResponseEntity<EatsOrderResponseDto>> createOrder(@RequestBody Mono<CreateOrderRequest> mono) {
        return mono.flatMap(orderService::createAndSaveOrder)
            .map(ResponseEntity.accepted()::body);
    }

    @PostMapping("/test")
    public ResponseEntity<String> testPost() {

        return ResponseEntity.ok("done");


    }

}
