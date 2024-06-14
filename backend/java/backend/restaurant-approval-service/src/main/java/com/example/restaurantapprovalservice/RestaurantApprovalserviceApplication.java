package com.example.restaurantapprovalservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"com.example.common.data",
        "com.example.commondata",
        "com.example.common.config",
        "com.example.kafka.admin",
        "com.example.kafka.config.data",
        "com.example.kafkaconsumer",
        "com.example.kafkaproducer",
    "com.example.restaurantapprovalservice"
})
public class RestaurantApprovalserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestaurantApprovalserviceApplication.class, args);
    }

}
