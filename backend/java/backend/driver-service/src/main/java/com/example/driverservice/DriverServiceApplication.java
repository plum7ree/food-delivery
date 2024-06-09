package com.example.driverservice;

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
        "com.example.driverservice"
})
public class DriverServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverServiceApplication.class, args);
    }

}
