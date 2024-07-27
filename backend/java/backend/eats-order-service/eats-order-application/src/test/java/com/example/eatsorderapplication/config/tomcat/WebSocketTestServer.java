package com.example.eatsorderapplication.config.tomcat;

import org.springframework.web.context.WebApplicationContext;


public interface WebSocketTestServer {

    int getPort();

    void deployDispatcherServlet(WebApplicationContext cxt);

    void undeployConfig();

    void start() throws Exception;

    void stop() throws Exception;

}