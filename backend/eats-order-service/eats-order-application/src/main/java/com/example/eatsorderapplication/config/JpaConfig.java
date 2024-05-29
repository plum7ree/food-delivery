package com.example.eatsorderapplication.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.example.eatsorderdataaccess.repository")
@EntityScan(basePackages = "com.example.eatsorderdataaccess.entity")
public class JpaConfig {
}
