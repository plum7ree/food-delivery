package com.example.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }


    @Bean
    public RouteLocator RouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
                // Ant 스타일 경로 패턴
                // * 하나의 세그먼트
                // ** 모든 경로.
                // 1. /driver/** 의 url 들에 대해서,
                .route(p -> p.path("/driver/**")
                        // Regex
                        // (?<name>...) : named capture group. ... 부분을 name 으로 캡쳐
                        // .* : 0 개 이상의 문자.
                        // 2. 이부분만 path 으로 캡쳐해서 : (?<path>.*)
                        // 3. /${path} 으로 보내라.
                        .filters(f -> f.rewritePath("/driver/(?<path>.*)", "/${path}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        //TODO 소문자
                        .uri("lb://DRIVER"))
                .route(p -> p.path("/route/**")
                        .filters(f -> f.rewritePath("/route/(?<path>.*)", "/%{path}")
                                .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                        .uri("lb://ROUTE")).
                build();

    }

}
