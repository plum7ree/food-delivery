
package com.example.eatsorderapplication.config;

import com.example.eatsorderapplication.config.tomcat.TomcatWebSocketTestServer;
import com.example.eatsorderapplication.service.EatsOrderCommandService;
import com.example.eatsorderapplication.service.listener.kafka.RestaurantApprovalResponseKafkaConsumer;
import com.example.eatsorderapplication.service.scheduler.RestaurantApprovalRequestOutboxScheduler;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

// ref: https://github.com/rstoyanchev/spring-websocket-portfolio/blob/main/src/test/java/org/springframework/samples/portfolio/web/tomcat/IntegrationPortfolioTests.java
@Slf4j
public class WebSocketConfigTest {
    private static TomcatWebSocketTestServer server;

    private static SockJsClient sockJsClient;

    @Configuration
    static class MyConfig {
        @Bean
        public RestaurantApprovalResponseKafkaConsumer restaurantApprovalResponseKafkaConsumer() {
            return Mockito.mock(RestaurantApprovalResponseKafkaConsumer.class);
        }

        @Bean
        public EatsOrderCommandService eatsOrderCommandService() {
            return Mockito.mock(EatsOrderCommandService.class);
        }

        @Bean
        public RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository() {
            return Mockito.mock(RestaurantApprovalRequestOutboxRepository.class);
        }

        @Bean
        public RestaurantApprovalRequestOutboxScheduler restaurantApprovalRequestOutboxScheduler() {
            return Mockito.mock(RestaurantApprovalRequestOutboxScheduler.class);
        }

    }


//    @Configuration
//    @EnableWebMvc
//    static class WebConfig implements WebMvcConfigurer {
//        @Bean
//        public ServletWebServerFactory servletWebServerFactory() {
//            return new TomcatServletWebServerFactory();
//        }
//    }
//
//    @Controller
//    static class TestController {
//        // publish topic name 과 subscribe topic name 을 다르게 할때 유용할듯
//        // 또한 중간에 작업 (저장, 수정) 할 떄도 유용.
//        @MessageMapping("/hello")
//        @SendTo("/topic/greetings")
//        public String greeting(String message) {
//            log.info("Greeting method called with message: {}", message);
//            return "Hello, " + message + "!";
//        }
//
//
//    }
//
//    @Configuration
//    @EnableWebSocketMessageBroker
//    public static class WebSocketBrokerConfig implements WebSocketMessageBrokerConfigurer {
//        @Override
//        public void configureMessageBroker(MessageBrokerRegistry config) {
//            config.enableSimpleBroker("/queue", "/topic");
//            config.setApplicationDestinationPrefixes("/app");
//        }
//
//        @Override
//        public void registerStompEndpoints(StompEndpointRegistry registry) {
//            registry.addEndpoint("/ws").setAllowedOrigins("*");
//            registry.addEndpoint("/sockjs").setAllowedOrigins("*").withSockJS();
//        }
//    }

//
//    @Test
//    public void testControllerRegistration() {
//        String[] controllerNames = applicationContext.getBeanNamesForAnnotation(Controller.class);
//        System.out.println("Found Controllers:");
//        for (String name : controllerNames) {
//            System.out.println(" - " + name + ": " + applicationContext.getBean(name).getClass().getName());
//        }
//
//        // 모든 빈 정의 출력
//        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
//        for (String beanName : beanFactory.getBeanDefinitionNames()) {
//            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
//            System.out.println("Bean: " + beanName + ", Type: " + beanDefinition.getBeanClassName());
//        }
//
//
//        assertTrue(applicationContext.containsBean("webSocketConfigTest.TestController"), "webSocketConfigTest.TestController bean should be registered");
//
//        Object controller = applicationContext.getBean("webSocketConfigTest.TestController");
//        assertNotNull(controller, "webSocketConfigTest.TestController should not be null");
////        assertTrue(controller instanceof TestController, "Bean should be an instance of TestController");
//
//        Method greetingMethod = ReflectionUtils.findMethod(TestController.class, "greeting", String.class);
//        assertNotNull(greetingMethod, "greeting method should exist");
//        assertTrue(greetingMethod.isAnnotationPresent(MessageMapping.class), "greeting method should have @MessageMapping");
//        assertTrue(greetingMethod.isAnnotationPresent(SendTo.class), "greeting method should have @SendTo");
//
//        Map<String, WebSocketHandler> handlers = applicationContext.getBeansOfType(WebSocketHandler.class);
//        assertFalse(handlers.isEmpty(), "WebSocket handlers should be registered");
//        handlers.forEach((name, handler) -> log.info("Registered WebSocket handler: {}", name));
//
//    }

    @Test
    public void testWebSocketConnection() throws ExecutionException, InterruptedException, TimeoutException {

        WebSocketStompClient stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new StringMessageConverter());

        String url = "ws://localhost:" + port + "/ws";
        log.info("Connecting to URL: {}", url);

        CompletableFuture<String> resultFuture = new CompletableFuture<>();

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                log.info("Connected to WebSocket server");

                session.subscribe("/topic/greetings", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;
                    }

                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        log.info("Received message: {}", payload);
                        resultFuture.complete((String) payload);
                    }
                });

                log.info("Sending message to /app/hello");
                session.send("/app/hello", "Spring");
            }

            @Override
            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
                log.error("Error in session: ", exception);
            }

            @Override
            public void handleTransportError(StompSession session, Throwable exception) {
                log.error("Transport error: ", exception);
            }
        };

        stompClient.connect(url, sessionHandler);

        String result = resultFuture.get(1000, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals("Hello, Spring!", result);
    }
}