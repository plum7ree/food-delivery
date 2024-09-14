package com.example.eatsorderapplication.application.controller;

import com.example.commondata.dto.order.CreateOrderRequestDto;
import com.example.eatsorderapplication.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class EatsOrderController {

    @Autowired
    private final OrderService orderService;


    @PostMapping("/createorder")
    public Mono<ResponseEntity<String>> createOrder(@RequestBody CreateOrderRequestDto dto) {
        return orderService.createAndSaveOrder(dto)
            .flatMap(e -> Mono.just(ResponseEntity.ok("")));
    }

    @GetMapping("/get-test")
    public Mono<ResponseEntity<String>> testGet() {

        return Mono.just(ResponseEntity.ok("done"));


    }
}
