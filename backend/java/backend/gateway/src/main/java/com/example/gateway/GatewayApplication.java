package com.example.gateway;


import com.example.gateway.config.UserServiceAuthenticationFilter;
//import com.example.gateway.config.WebSocketJwtAuthenticationFilter;
import com.example.gateway.config.WebSocketJwtAuthenticationFilter;
import com.example.gateway.configdata.LBUriConfigData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@EnableConfigurationProperties
@SpringBootApplication
public class GatewayApplication {
    @Autowired
    private WebSocketJwtAuthenticationFilter webSocketJwtAuthenticationFilter;

    @Autowired
    private LBUriConfigData lbUriConfigData;

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

    @Bean
    public RouteLocator RouteConfig(RouteLocatorBuilder routeLocatorBuilder) {
        return routeLocatorBuilder.routes()
            .route(p -> p.path("/driver/**")
                .filters(f -> f.rewritePath("/driver/(?<path>.*)", "/${path}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri("lb://driver"))
            .route(p -> p.path("/route/**")
                .filters(f -> f.rewritePath("/route/(?<path>.*)", "/${path}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri("lb://route"))
            .route(p -> p.path("/user/**")
                .filters(f -> f.rewritePath("/user/(?<path>.*)", "/${path}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
                    .filter(new UserServiceAuthenticationFilter()))
                .uri("lb://user"))
            .route(p -> p.path("/eatssearch/**")
                .filters(f -> f.rewritePath("/eatssearch/(?<path>.*)", "/${path}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri("lb://eatssearch"))
            .route(p -> p.path("/eatsorder/**")
                .filters(f -> f.rewritePath("/eatsorder/(?<path>.*)", "/${path}")
                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri("lb://eatsorder"))
            .route(p -> p.path("/ws/**")
//                .filters(f -> f
                // oauth2ResourceServer(jwt -> .jwt 보다 먼저 불리는거 보장 어떻게??
//                    .filter(webSocketJwtAuthenticationFilter)
//                    .tokenRelay() // 이미 검증된 토큰을 다음 서비스로 전달
//                    .filter(new UserServiceAuthenticationFilter())
//                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri(lbUriConfigData.getWebSocketWsServerUri()))
            .route(p -> p.path("/sockjs/**")
//                .filters(f -> f
//                    .filter(webSocketJwtAuthenticationFilter)
//                    .tokenRelay()
//                    .filter(new UserServiceAuthenticationFilter())
//                    .addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
                .uri(lbUriConfigData.getWebSocketSockjsServerUri()))
//            .route(p -> p.path("/**")
//                .filters(f -> f.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
//                .uri("forward:/")) // 이게 stack overflow error 를 발생시킨다. root 로 forward 하지 말자.
            .build();
    }
}