package com.example.couponservice.kafka.listener;

import com.example.couponservice.kafka.listener.CouponIssueRequestKafkaListener;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LoadTest {

    @Autowired
    private CouponIssueRequestKafkaListener couponIssueRequestKafkaListener;

    @Test
    void shouldLoadCouponIssueRequestKafkaListener() {
        assertThat(couponIssueRequestKafkaListener).isNotNull();
    }
}