package com.example.eatsorderapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
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
        "com.example.eatsorderapplication",
})
@EnableJpaRepositories(basePackages = "com.example.eatsorderdataaccess.repository")
@EntityScan(basePackages = "com.example.eatsorderdataaccess.entity")
public class CallApplication {

    public static void main(String[] args) {
        SpringApplication.run(CallApplication.class, args);
    }

}

