package com.example.eatsorderdataaccess.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@EntityScan(basePackages = "com.example.eatsorderdataaccess.entity")
@EnableJpaRepositories(basePackages = "com.example.eatsorderdataaccess.repository")
public class TestConfig {
}
