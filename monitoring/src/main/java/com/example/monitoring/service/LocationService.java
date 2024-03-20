package com.example.monitoring.service;

import com.example.driver.controller.LocationController;
import com.example.driver.data.dto.LocationDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microservices.demo.kafka.avro.model.LocationAvroModel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@RequiredArgsConstructor
public class LocationService {
    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    private SimpMessagingTemplate simpMessagingTemplate;
    public void sendLocation(LocationDto location) {
        log.info("sending location");
        simpMessagingTemplate.convertAndSend(
                "/topic/location",
                location.toString()
        );
    }
}