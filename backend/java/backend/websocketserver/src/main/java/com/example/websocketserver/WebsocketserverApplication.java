package com.example.websocketserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WebsocketserverApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebsocketserverApplication.class, args);
    }

}
