package com.example.eatsorderapplication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties()
@EnableScheduling()
public class EatsOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(EatsOrderApplication.class, args);
    }

}

