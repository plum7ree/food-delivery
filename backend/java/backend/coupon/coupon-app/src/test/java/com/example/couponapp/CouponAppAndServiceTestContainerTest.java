//package com.example.couponapp;
//
//
//import com.example.couponapp.dto.IssueRequestDto;
//import com.example.couponapp.dto.ResponseDto;
//import com.example.couponapp.dto.Status;
//import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
//import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.boot.test.web.server.LocalServerPort;
//import org.springframework.context.ApplicationContextInitializer;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Import;
//import org.springframework.context.annotation.Primary;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.web.reactive.function.BodyInserters;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.testcontainers.containers.DockerComposeContainer;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.containers.PostgreSQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import reactor.core.publisher.Mono;
//
//import java.io.File;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor.applyTo;
//
//@Testcontainers
//@EmbeddedKafka(partitions = 1, topics = {"coupon-issue-request-topic"}, brokerProperties = "listeners=PLAINTEXT://localhost:49092")
//public class CouponAppAndServiceTestContainerTest {
//
//
//    private static final Network network = Network.newNetwork();
//
//    @Container
//    public static GenericContainer<?> postgresContainer = new GenericContainer<>("postgres:13")
//        .withNetwork(network)
//        .withNetworkAliases("coupon-postgres")
//        .withEnv("POSTGRES_USER", "postgres")
//        .withEnv("POSTGRES_PASSWORD", "admin")
//        .withEnv("POSTGRES_DB", "postgres")
//        .withExposedPorts(5432); // Ensure the default PostgreSQL port is exposed
//
//
//    @Container
//    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.2.6")
//        .withNetwork(network)
//        .withNetworkAliases("coupon-redis")
//        .withExposedPorts(6379);
//
//    @Container
//    public static GenericContainer<?> couponServiceContainer = new GenericContainer<>("lomojiki/uber-msa-coupon-service:latest")
//        .withNetwork(network)
//        .withNetworkAliases("coupon-service")
//        .withExposedPorts(8093)  // Expose the port on which the coupon-service is running
//        .dependsOn(postgresContainer, redisContainer);
//
//    @Container
//    public static GenericContainer<?> couponAppContainer = new GenericContainer<>("lomojiki/uber-msa-coupon-app:latest")
//        .withNetwork(network)
//        .withNetworkAliases("coupon-app")
//        .withExposedPorts(8092)  // Expose the port on which the coupon-service is running
//        .dependsOn(postgresContainer, redisContainer);
//
//    @DynamicPropertySource
//    public void init() {
//        postgresContainer.start();
//        redisContainer.start();
//        couponServiceContainer.start();
//        couponAppContainer.start();
//    }
//
//
//    @Test
//    public void testCouponIssueRequest() throws InterruptedException {
//        WebClient webClient = WebClient.builder()
//            .baseUrl("http://localhost:" + couponAppContainer.getMappedPort(8092))
//            .build();
//
//        IssueRequestDto issueRequestDto = new IssueRequestDto();
//        issueRequestDto.setUserId("1000000");
//        issueRequestDto.setCouponId(123L);
//
//        Mono<ResponseEntity<ResponseDto>> response = webClient.post()
//            .uri("/api/issue")
//            .body(BodyInserters.fromValue(issueRequestDto))
//            .retrieve()
//            .toEntity(ResponseDto.class);
//
//        Thread.sleep(199999999);
//        ResponseEntity<ResponseDto> responseEntity = response.block();
//
//        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(responseEntity.getBody()).isNotNull();
//        assertThat(responseEntity.getBody().getStatus()).isEqualTo(Status.SUCCESSFUL);
//
//        // Additional logic to verify that the message was sent to the Kafka topic can be added here
//    }
//
//    @Configuration
//    public class TestConfig {
//
//        @Bean
//        @Primary
//        public SchemaRegistryClient schemaRegistryClient() {
//            return new MockSchemaRegistryClient();
//        }
//    }
//
//}
