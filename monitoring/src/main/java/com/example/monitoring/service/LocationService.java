package com.example.monitoring.service;

import com.example.driver.controller.LocationController;
import com.example.driver.data.dto.LocationDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.kafka.avro.model.LocationAvroModel;
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
private final ObjectMapper objectMapper;
    private final SimpMessagingTemplate simpMessagingTemplate;
    public void sendLocation(LocationDto location) {
        log.info("LocationService websocket sending location");

        try {
            String json = objectMapper.writeValueAsString(location);
            simpMessagingTemplate.convertAndSend(
                    "/topic/location",
                    json
            );

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

    }


}