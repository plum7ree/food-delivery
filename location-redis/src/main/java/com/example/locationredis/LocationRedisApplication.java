package com.example.locationredis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@SpringBootApplication
// make sure kafka.producer "not" included.
// 내부에 존재하는 @Configuration 때문에 config server 에서 producer 관련 값이 없으니깐 에러를 호출함.
// 이 패키지는 Consumer 만 존재하므로 제외시켜야함.
@ComponentScan({"com.example.common.data",
        "com.example.common.config",
        "com.example.kafka.admin",
        "com.example.kafka.config.data",
        "com.example.kafkaconsumer",
        "com.example.locationredis"} )
public class LocationRedisApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocationRedisApplication.class, args);
    }

}
