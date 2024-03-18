package com.example.locationredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
@EnableConfigurationProperties() // ComponentScan not replaces this
@ComponentScan(basePackages = {"com.example"})
public class LocationRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocationRedisApplication.class, args);
    }

}
