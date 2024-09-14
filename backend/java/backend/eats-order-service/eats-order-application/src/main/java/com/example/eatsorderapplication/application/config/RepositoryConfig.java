package com.example.eatsorderapplication.application.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories(basePackages = "com.example.eatsorderdataaccess.repository")
@EntityScan(basePackages = "com.example.eatsorderdataaccess.entity")
public class RepositoryConfig {
}
