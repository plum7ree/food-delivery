package com.example.gateway.configdata;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lb-uri-config")
public class LBUriConfigData {
//lb-uri-config:
//  web-socket-ws-server-uri: "lb:ws://localhost:8094/ws"
//  web-socket-sockjs-server-uri: "lb://localhost:8094/sockjs"

    private String WebSocketWsServerUri;
    private String WebSocketSockjsServerUri;
}
