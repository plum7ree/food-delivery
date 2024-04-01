package com.example.gateway.service.client;

import org.aspectj.weaver.ast.Call;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient
public interface CallServiceFeignClient {

    // request header
    // CORRELATION_ID 가 왜 있어야하지?
    // @RequestHeader("uber-correlation-id") String correlationId
    @GetMapping(value = "/api/fetch", consumes = "application/json")
    public ResponseEntity<Call> fetchMostRecentCallStatus(@RequestParam UUID userId);

}

