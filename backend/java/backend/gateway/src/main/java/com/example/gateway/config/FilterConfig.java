package com.example.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Bean
    public UserServiceAuthenticationFilter userServiceAuthenticationFilter() {
        return new UserServiceAuthenticationFilter();
    }
}