package com.example.eatsorderapplication.application.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.example.common.data",
        "com.example.common.config",
        "com.example.commondata",
        "com.example.eatsorderdomain",
        "com.example.eatsorderdataaccess",
        "com.example.eatsorderapplication",
})
public class ComponentScanConfig {
}