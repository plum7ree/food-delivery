package com.example.couponservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
    "com.example.kafka.config.data",
    "com.example.kafkaconsumer",
})
public class ComponentScanConfig {
}