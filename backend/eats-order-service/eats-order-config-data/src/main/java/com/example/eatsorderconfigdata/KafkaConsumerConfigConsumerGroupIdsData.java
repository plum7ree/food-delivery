package com.example.eatsorderconfigdata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "kafka-consumer-group-id")
public class KafkaConsumerConfigConsumerGroupIdsData {
    private String paymentConsumerGroupId;
    private String driverApprovalConsumerGroupId;
}
