package com.example.restaurantapprovalservice.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "call-service")
public class CallServiceConfigData {
    private String paymentRequestTopicName;
    private String paymentResponseTopicName;
}
