package com.example.gateway;

import com.example.gateway.config.UserServiceAuthenticationFilter;
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
                .filters(f -> f.rewritePath("/route/(?<path>.*)", "/${path}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri("lb://ROUTE"))
            .route(p -> p.path("/user/**")
                .filters(f -> f.rewritePath("/user/(?<path>.*)", "/${path}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                    .filter(new UserServiceAuthenticationFilter()))
                .uri("lb://USER"))
            .route(p -> p.path("/eatssearch/**")
                .filters(f -> f.rewritePath("/eatssearch/(?<path>.*)", "/${path}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri("lb://EATSSEARCH"))
            .route(p -> p.path("/eatsorder/**")
                .filters(f -> f.rewritePath("/eatsorder/(?<path>.*)", "/${path}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri("lb://EATSORDER"))
            .route(p -> p.path("/websocket/**")
                .uri("lb:ws://MONITORING"))
            .route(p -> p.path("/sockjs/**")
                .uri("lb://MONITORING"))
            .route(p -> p.path("/**") // rest of the request into this server.
                .filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri("forward:/"))
            .build();

    }

}
