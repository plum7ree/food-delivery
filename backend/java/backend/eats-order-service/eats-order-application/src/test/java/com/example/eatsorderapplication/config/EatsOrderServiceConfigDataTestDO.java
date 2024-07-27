package com.example.eatsorderapplication.config;

import com.example.eatsorderapplication.EatsOrderApplication;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = EatsOrderApplication.class)
@EmbeddedKafka(partitions = 1, ports = {9092})
@TestPropertySource(locations = "classpath:application.yml")
@ActiveProfiles("test")
public class EatsOrderServiceConfigDataTestDO {

    @Autowired
    private EatsOrderServiceConfigData eatsOrderServiceConfigData;


    @Test
    public void testCallServiceConfigDataLoading() {
        assertEquals("payment-request-topic", eatsOrderServiceConfigData.getPaymentRequestTopicName());
        assertEquals("payment-response-topic", eatsOrderServiceConfigData.getPaymentResponseTopicName());
        assertEquals("restaurant-approval-request-topic", eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName());
        assertEquals("restaurant-approval-response-topic", eatsOrderServiceConfigData.getRestaurantApprovalResponseTopicName());
//        assertEquals("restaurant-approval-request-topic", callServiceConfigData.getRestaurantApprovalRequestTopicName());
    }

}