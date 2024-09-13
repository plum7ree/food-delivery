package com.example.websocketserver.application.config;

import com.example.websocketserver.application.data.dto.DriverDetailsDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class DriverMatchingMapConfig {
    @Bean(name = "driverMatchingMap")
    public ConcurrentHashMap<String, DriverDetailsDto> driverMatchingMap() {
        return new ConcurrentHashMap<>();
    }
}