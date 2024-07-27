package com.example.restaurantapprovalservice.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.example.common.data",
    "com.example.common.config",
    "com.example.commondata",
    "com.example.kafka.admin",
    "com.example.kafka.config.data",
    "com.example.kafkaconsumer",
    "com.example.kafkaproducer",
    "com.example.eatsorderdomain",
    "com.example.eatsorderdataaccess",
    "com.example.eatsorderconfigdata",
    "com.example.restaurantapprovalservice"
})
public class ComponentScanConfig {
}
