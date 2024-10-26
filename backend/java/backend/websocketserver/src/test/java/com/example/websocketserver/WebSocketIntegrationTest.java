package com.example.websocketserver;

import com.example.websocketserver.application.data.entity.Account;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import org.springframework.web.socket.sockjs.client.SockJsClient;

import java.sql.Connection;
import java.util.Base64;
import java.util.UUID;

public class WebSocketIntegrationTest {


    private final String WEBSOCKET_URL = "ws://localhost:8080/ws"; // WebSocket 서버 URL
    private final String JWT_TOKEN_PREFIX = "Bearer "; // JWT 토큰 프리픽스
    private String testUserId;
    private String testJwtToken;
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    public void setUp() throws Exception {
        // JDBC DataSource 설정
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres?currentSchema=user_schema");
        dataSource.setUsername("postgres");
        dataSource.setPassword("admin");

        jdbcTemplate = new JdbcTemplate(dataSource);

        // SQL 파일 로드 및 실행
        try (Connection connection = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(connection, new ClassPathResource("init_data.sql"));
        }
    }

    @AfterEach
    public void tearDown() {
        // 테스트용 사용자 삭제
    }

    @Test
    public void testWebSocketConnectionWithJwt() throws Exception {
        // WebSocket client 생성
        StandardWebSocketClient webSocketClient = new StandardWebSocketClient();

        // WebSocketStompClient 생성
        WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

        // STOMP 연결 설정
        StompSessionHandler sessionHandler = new MyStompSessionHandler();

        String url = "ws://localhost:8080/ws";
        stompClient.connect(url, sessionHandler).get();
    }

    private String generateJwtToken(String subject) {
        // JWT 생성 로직 구현 (예: HMAC 또는 RSA 서명 사용)
        String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
        String payload = String.format("{\"sub\":\"%s\",\"iss\":\"test-issuer\",\"exp\":%d}", subject, System.currentTimeMillis() + 3600000);

        String encodedHeader = Base64.getUrlEncoder().encodeToString(header.getBytes());
        String encodedPayload = Base64.getUrlEncoder().encodeToString(payload.getBytes());
        String signature = "dummy-signature"; // 실제 서명 로직 추가

        return JWT_TOKEN_PREFIX + encodedHeader + "." + encodedPayload + "." + signature;
    }

    private StompHeaders createHeaders(String jwtToken) {
        StompHeaders headers = new StompHeaders();
        headers.add("Authorization", jwtToken); // JWT 헤더 추가
        return headers;
    }

    private class MyStompSessionHandler extends StompSessionHandlerAdapter {
        @Override
        public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
            System.out.println("Connected: " + session.getSessionId());
            // 필요한 경우 구독하거나 다른 작업 수행
        }

        @Override
        public void handleFrame(StompHeaders headers, Object payload) {
            // 수신한 메시지 처리
            System.out.println("Received: " + payload);
        }

        @Override
        public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
            // 예외 처리
            exception.printStackTrace();
        }

        @Override
        public void handleTransportError(StompSession session, Throwable exception) {
            // 전송 오류 처리
            exception.printStackTrace();
        }
    }
}
