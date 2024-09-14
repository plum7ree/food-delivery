//package com.example.eatsorderapplication.config;
//
//
//import com.example.eatsorderapplication.config.tomcat.DispatcherServletInitializer;
//import com.example.eatsorderapplication.config.tomcat.TomcatWebSocketTestServer;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
//import org.junit.AfterClass;
//import org.junit.BeforeClass;
//import org.junit.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.MediaType;
//import org.springframework.http.converter.FormHttpMessageConverter;
//import org.springframework.messaging.converter.MappingJackson2MessageConverter;
//import org.springframework.messaging.simp.config.MessageBrokerRegistry;
//import org.springframework.messaging.simp.stomp.*;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
//import org.springframework.test.util.JsonPathExpectationsHelper;
//import org.springframework.test.util.TestSocketUtils;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.socket.WebSocketHttpHeaders;
//import org.springframework.web.socket.client.standard.StandardWebSocketClient;
//import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
//import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
//import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
//import org.springframework.web.socket.messaging.WebSocketStompClient;
//import org.springframework.web.socket.server.standard.TomcatRequestUpgradeStrategy;
//import org.springframework.web.socket.server.support.DefaultHandshakeHandler;
//import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
//import org.springframework.web.socket.sockjs.client.SockJsClient;
//import org.springframework.web.socket.sockjs.client.Transport;
//import org.springframework.web.socket.sockjs.client.WebSocketTransport;
//
//import java.lang.reflect.Type;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.CountDownLatch;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.atomic.AtomicReference;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.fail;
//
///**
// * ref: https://github.com/rstoyanchev/spring-websocket-portfolio/blob/main/src/test/java/org/springframework/samples/portfolio/web/tomcat/IntegrationPortfolioTests.java
// */
//@Slf4j
//public class WebSocketIntegrationTest {
//
//    private static Log logger = LogFactory.getLog(WebSocketIntegrationTest.class);
//
//    private static int port;
//
//    private static TomcatWebSocketTestServer server;
//
//    private static SockJsClient sockJsClient;
//
//    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();
//
//
//    @BeforeClass
//    public static void setup() throws Exception {
//
//        // Since test classpath includes both embedded Tomcat and Jetty we need to
//        // set a Spring profile explicitly to bypass WebSocket engine detection.
//        // See {@link org.springframework.samples.portfolio.config.WebSocketConfig}
//
//        // This test is not supported with Jetty because it doesn't seem to support
//        // deployment withspecific ServletContainerInitializer's at for testing
//
//        System.setProperty("spring.profiles.active", "test.tomcat");
//
//        port = TestSocketUtils.findAvailableTcpPort();
//
//        server = new TomcatWebSocketTestServer(port);
//        // 아래의 TestDispatcherServletInitializer 의 getServletConfigClasses 에서 WebSocketConfig 를 추가 하고,
//        // WebSocketConfig 에서 componenScan 해서 컨트롤러 추가하는듯.
//        server.deployWithInitializer(TestDispatcherServletInitializer.class, AbstractSecurityWebApplicationInitializer.class);
//        server.start();
//
//        List<Transport> transports = new ArrayList<>();
//        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
//        RestTemplateXhrTransport xhrTransport = new RestTemplateXhrTransport(new RestTemplate());
//        transports.add(xhrTransport);
//
//        sockJsClient = new SockJsClient(transports);
//    }
//
//
//    @AfterClass
//    public static void teardown() {
//        if (server != null) {
//            try {
//                server.undeployConfig();
//            } catch (Throwable t) {
//                logger.error("Failed to undeploy application", t);
//            }
//
//            try {
//                server.stop();
//            } catch (Throwable t) {
//                logger.error("Failed to stop server", t);
//            }
//        }
//    }
//
//
//    @Test
//    public void getPositions() throws Exception {
//
//        final CountDownLatch latch = new CountDownLatch(1);
//        final AtomicReference<Throwable> failure = new AtomicReference<>();
//
//        StompSessionHandler handler = new AbstractTestSessionHandler(failure) {
//
//            @Override
//            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
//                session.subscribe("/app/positions", new StompFrameHandler() {
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return byte[].class;
//                    }
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        String json = new String((byte[]) payload);
//                        logger.debug("Got " + json);
//                        try {
//                            new JsonPathExpectationsHelper("$[0].company").assertValue(json, "Citrix Systems, Inc.");
//                            new JsonPathExpectationsHelper("$[1].company").assertValue(json, "Dell Inc.");
//                            new JsonPathExpectationsHelper("$[2].company").assertValue(json, "Microsoft");
//                            new JsonPathExpectationsHelper("$[3].company").assertValue(json, "Oracle");
//                        } catch (Throwable t) {
//                            failure.set(t);
//                        } finally {
//                            session.disconnect();
//                            latch.countDown();
//                        }
//                    }
//                });
//            }
//        };
//
//        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
//        stompClient.connect("ws://localhost:{port}/portfolio", this.headers, handler, port);
//
//        if (failure.get() != null) {
//            throw new AssertionError("", failure.get());
//        }
//
//        if (!latch.await(5, TimeUnit.SECONDS)) {
//            fail("Portfolio positions not received");
//        }
//    }
//
//    @Test
//    public void executeTrade() throws Exception {
//
//        final CountDownLatch latch = new CountDownLatch(1);
//        final AtomicReference<Throwable> failure = new AtomicReference<>();
//
//        StompSessionHandler handler = new AbstractTestSessionHandler(failure) {
//
//            @Override
//            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
//                log.info("client endpoint connected!");
//                session.subscribe("/user/queue/position-updates", new StompFrameHandler() {
//                    @Override
//                    public Type getPayloadType(StompHeaders headers) {
//                        return String.class; // Expecting text messages
//                    }
//
//                    @Override
//                    public void handleFrame(StompHeaders headers, Object payload) {
//                        String message = (String) payload;
//                        logger.debug("Got " + message);
//                        try {
//                            // Example assertion for text message
//                            assertEquals("Trade confirmed", message);
//                        } catch (Throwable t) {
//                            failure.set(t);
//                        } finally {
//                            session.disconnect();
//                            latch.countDown();
//                        }
//                    }
//                });
//
//                try {
//                    String tradeMessage = "Buy DELL 25"; // Your text message
//                    session.send("/app/trade", tradeMessage);
//                } catch (Throwable t) {
//                    failure.set(t);
//                    latch.countDown();
//                }
//            }
//        };
//
//        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        stompClient.connect("ws://localhost:{port}/sockjs", headers, handler, port);
//
//        if (!latch.await(10, TimeUnit.SECONDS)) {
//            fail("Trade confirmation not received");
//        } else if (failure.get() != null) {
//            throw new AssertionError("", failure.get());
//        }
//    }
//
//
//    public static class TestDispatcherServletInitializer extends DispatcherServletInitializer {
//
//        @Override
//        protected Class<?>[] getServletConfigClasses() {
//            return new Class[]{TestWebSocketConfig.class};
//        }
//    }
//
//    @Configuration
//	@EnableScheduling
//    여기서 Controller 추가
//	@ComponentScan(
//			basePackages="org.springframework.samples",
//			excludeFilters = @ComponentScan.Filter(type= FilterType.ANNOTATION, value = Configuration.class)
//	)
//	@EnableWebSocketMessageBroker
//    static class TestWebSocketConfig implements WebSocketMessageBrokerConfigurer {
//
//        @Autowired
//        Environment env;
//
//        @Override
//        public void registerStompEndpoints(StompEndpointRegistry registry) {
//            // The test classpath includes both Tomcat and Jetty, so let's be explicit
//            DefaultHandshakeHandler handler = new DefaultHandshakeHandler(new TomcatRequestUpgradeStrategy());
//            registry.addEndpoint("/sockjs").setHandshakeHandler(handler).withSockJS();
//            registry.addEndpoint("/ws").setHandshakeHandler(handler);
//        }
//
//        @Override
//        public void configureMessageBroker(MessageBrokerRegistry registry) {
//            registry.enableSimpleBroker("/queue/", "/topic/");
//            registry.setApplicationDestinationPrefixes("/app");
//        }
//    }
//
//
//    /**
//     * used in client side
//     */
//    private static abstract class AbstractTestSessionHandler extends StompSessionHandlerAdapter {
//
//        private final AtomicReference<Throwable> failure;
//
//
//        public AbstractTestSessionHandler(AtomicReference<Throwable> failure) {
//            this.failure = failure;
//        }
//
//        @Override
//        public void handleFrame(StompHeaders headers, Object payload) {
//            logger.error("STOMP ERROR frame: " + headers.toString());
//            this.failure.set(new Exception(headers.toString()));
//        }
//
//        @Override
//        public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
//            logger.error("Handler exception", ex);
//            this.failure.set(ex);
//        }
//
//        @Override
//        public void handleTransportError(StompSession session, Throwable ex) {
//            logger.error("Transport failure", ex);
//            this.failure.set(ex);
//        }
//    }
//
//}