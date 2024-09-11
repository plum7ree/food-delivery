package com.example.eatsorderapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties
@EnableScheduling
@EnableAsync
@EnableFeignClients(basePackages = "com.example.eatsorderapplication.service.client")
public class EatsOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EatsOrderApplication.class, args);
    }

}

