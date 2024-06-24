package com.example.couponapp.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({
    "com.example.kafka.config.data",
    "com.example.kafkaproducer",
})
public class ComponentScanConfig {
}
