package com.example.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
    @ComponentScan({"com.example.common.data",
        "com.example.common.config",
            "com.example.commondata",
        "com.example.kafka.admin",
        "com.example.kafka.config.data",
        "com.example.kafkaconsumer",
        "com.example.kafkaproducer"})
public class PaymentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentServiceApplication.class, args);
    }

}
