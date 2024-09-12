package com.example.eatsorderapplication.application.config;

import feign.Request;
import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public Retryer retryer() {
        // 재시도를 하지 않도록 설정 (Retryer.NEVER_RETRY)
        return Retryer.NEVER_RETRY;
    }

    @Bean
    public Request.Options requestOptions() {
        // 연결 및 읽기 타임아웃 설정 (밀리초 단위)
        int connectTimeoutMillis = 5000; // 연결 타임아웃 5초
        int readTimeoutMillis = 5000;    // 읽기 타임아웃 5초
        return new Request.Options(connectTimeoutMillis, readTimeoutMillis);
    }
}
