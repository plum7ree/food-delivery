package com.example.eatsorderconfigdata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "topic-names")
public class EatsOrderServiceConfigData {
    String paymentRequestTopicName;
    String paymentResponseTopicName;
    String restaurantApprovalRequestTopicName;
    String restaurantApprovalResponseTopicName;
}
