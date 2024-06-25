//package com.example.couponapp;
//
//
//import io.confluent.kafka.schemaregistry.client.MockSchemaRegistryClient;
//import io.confluent.kafka.schemaregistry.client.SchemaRegistryClient;
//import org.junit.jupiter.api.Test;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.kafka.test.context.EmbeddedKafka;
//import org.testcontainers.containers.DockerComposeContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.containers.wait.strategy.Wait;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.io.File;
//import java.time.Duration;
//
//@Testcontainers
//@EmbeddedKafka(partitions = 1, topics = {"coupon-issue-request-topic"}, brokerProperties = "listeners=PLAINTEXT://localhost:49092")
//public class CouponAppAndServiceDockerComposeTestContainerTest_backup {
//
//    private static final Network network = Network.newNetwork();
//
//    static DockerComposeContainer postgresContainer;
//
//    static {
//        postgresContainer = new DockerComposeContainer(new File(ClassLoader.getSystemResource("docker-compose-test.yml").getFile()))
//            .withExposedService(
//                "coupon-db",
//                5432,
//                Wait.forLogMessage(".*ready to accept connections.*", 1)
//                    .withStartupTimeout(Duration.ofSeconds(200))
//            );
//        postgresContainer.start();
//
//    }
//
//
////    DockerComposeContainer Doesn't work for redis with version error.
////    static DockerComposeContainer redisContainer;
////
////    static {
////        redisContainer = new DockerComposeContainer(new File(ClassLoader.getSystemResource("redis.yml").getFile()))
////            .withExposedService(
////                "coupon-redis",
////                6379,
////                Wait.forLogMessage(".*Ready to accept connections.*", 1)
////                    .withStartupTimeout(Duration.ofSeconds(200))
////            ).withLocalCompose(true);
////        redisContainer.start();
////
////    }
//
////    @Container
////    public static GenericContainer<?> redisContainer = new GenericContainer<>("redis:6.2.6")
////        .withNetworkAliases("coupon-redis")
////        .withExposedPorts(6379)
////        .waitingFor(Wait.forLogMessage(".*Ready to accept connections.*", 1)
////            .withStartupTimeout(Duration.ofSeconds(200)));
////
////    static {
////        redisContainer.start();
////    }
//
//
////    @Container
////    public static GenericContainer<?> couponServiceContainer = new GenericContainer<>("lomojiki/uber-msa-coupon-service:latest")
////        .withNetworkAliases("coupon-service")
////        .withExposedPorts(8093)  // Expose the port on which the coupon-service is running
////        .dependsOn(postgresContainer, redisContainer);
////
////    static {
////        couponServiceContainer.start();
////    }
//
//
////    @Container
////    public static GenericContainer<?> couponAppContainer = new GenericContainer<>("lomojiki/uber-msa-coupon-app:latest")
////        .withNetwork(network)
////        .withNetworkAliases("coupon-app")
////        .withExposedPorts(8092)  // Expose the port on which the coupon-service is running
////        .dependsOn(postgresContainer, redisContainer);
////
////    static {
////        couponAppContainer.start();
////    }
//
////    @DynamicPropertySource
////    public void init() {
////        postgresContainer.start();
////        redisContainer.start();
////        couponServiceContainer.start();
////        couponAppContainer.start();
////    }
//
//
//    @Test
//    public void testCouponIssueRequest() throws InterruptedException {
////        WebClient webClient = WebClient.builder()
////            .baseUrl("http://localhost:" + couponAppContainer.getMappedPort(8092))
////            .build();
////
////        IssueRequestDto issueRequestDto = new IssueRequestDto();
////        issueRequestDto.setUserId("1000000");
////        issueRequestDto.setCouponId(123L);
////
////        Mono<ResponseEntity<ResponseDto>> response = webClient.post()
////            .uri("/api/issue")
////            .body(BodyInserters.fromValue(issueRequestDto))
////            .retrieve()
////            .toEntity(ResponseDto.class);
////
//        Thread.sleep(199999999);
////        ResponseEntity<ResponseDto> responseEntity = response.block();
////
////        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
////        assertThat(responseEntity.getBody()).isNotNull();
////        assertThat(responseEntity.getBody().getStatus()).isEqualTo(Status.SUCCESSFUL);
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
