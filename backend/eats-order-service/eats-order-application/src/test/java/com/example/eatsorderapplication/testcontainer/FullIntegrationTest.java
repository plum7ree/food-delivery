package com.example.eatsorderapplication.testcontainer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;

// https://testcontainers.com/guides/getting-started-with-testcontainers-for-java/
@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FullIntegrationTest {

    private static Network network = Network.newNetwork();

    @Container
    public static PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("user")
            .withPassword("password")
            .withNetwork(network)
            .waitingFor(Wait.forListeningPort());

    @Container
    public static GenericContainer<?> configServer = new GenericContainer<>("lomojiki/uber-msa-configserver:latest")
            .withExposedPorts(8888)
            .withNetwork(network)
            .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200));

    @Container
    public static GenericContainer<?> eurekaServer = new GenericContainer<>("lomojiki/uber-msa-eurekaserver:latest")
            .withExposedPorts(8761)
            .withNetwork(network)
            .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200))
            .dependsOn(configServer);

    @Container
    public static GenericContainer<?> eatsOrderApplication = new GenericContainer<>("lomojiki/uber-msa-eatsorderapplication:latest")
            .withExposedPorts(8081)
            .withNetwork(network)
            .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200))
            .dependsOn(eurekaServer, configServer, postgresContainer);

    @Container
    public static GenericContainer<?> gatewayServer = new GenericContainer<>("lomojiki/uber-msa-gatewayserver:latest")
            .withExposedPorts(8080)
            .withNetwork(network)
            .waitingFor(Wait.forHttp("/actuator/health").forStatusCode(200))
            .dependsOn(eatsOrderApplication, eurekaServer, configServer);

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeAll
    public static void setUp() {
        postgresContainer.start();
        configServer.start();
        eurekaServer.start();
        eatsOrderApplication.start();
        gatewayServer.start();
    }

    @AfterAll
    public static void tearDown() {
        gatewayServer.stop();
        eatsOrderApplication.stop();
        eurekaServer.stop();
        configServer.stop();
        postgresContainer.stop();
    }

    @Test
    public void testPostRequestToEatsOrder() {
        // Given
        String requestBody = "{ ... }"; // JSON 형태의 요청 바디
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // When
        String gatewayUrl = "http://" + gatewayServer.getHost() + ":" + gatewayServer.getMappedPort(8080) + "/eatsorder/api/eatsorder";
        ResponseEntity<String> response = restTemplate.exchange(
                gatewayUrl,
                HttpMethod.POST,
                entity,
                String.class);

        // Then
        assertEquals(200, response.getStatusCodeValue());
    }
}
