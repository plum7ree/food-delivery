package com.example.callconfigdata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-consumer-config")
public class CallServiceConfigData {
    String paymentRequestTopicName;
    String paymentResponseTopicName;
    String driverApprovalRequestTopicName;
    String driverApprovalResponseTopicName;
}
