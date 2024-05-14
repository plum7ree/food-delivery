package com.example.eatssearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableConfigurationProperties() // ComponentScan not replaces this
@ComponentScan(basePackages = {"com.example"})

public class EatsSearchApplication {
    public static void main(String[] args) {
        SpringApplication.run(EatsSearchApplication.class, args);
    }

}
