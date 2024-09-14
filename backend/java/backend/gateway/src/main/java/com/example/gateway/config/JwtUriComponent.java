package com.example.gateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@Getter
@Setter
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "jwt")
class JwtUriComponent {

    private HashMap<String, String> issuerToJwksUri;
}
