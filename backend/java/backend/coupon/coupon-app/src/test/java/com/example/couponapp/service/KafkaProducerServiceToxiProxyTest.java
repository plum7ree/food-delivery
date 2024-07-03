package com.example.couponapp.service;

import com.example.couponapp.dto.IssueRequestDto;
import com.example.kafka.admin.client.KafkaAdminClient;
import com.example.kafka.admin.config.KafkaAdminConfig;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import com.example.kafka.config.data.KafkaConfigData;
import com.example.kafka.config.data.KafkaProducerConfigData;
import com.example.kafkaproducer.KafkaProducer;
import com.example.kafkaproducer.config.KafkaProducerConfig;
import eu.rekawek.toxiproxy.model.ToxicDirection;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.ToxiproxyContainer;
import org.testcontainers.shaded.org.awaitility.Awaitility;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.testcontainers.shaded.org.awaitility.Awaitility.await;

class KafkaProducerServiceToxiProxyTest {

    private static final Network NETWORK = Network.newNetwork();
    private static final KafkaContainer KAFKA = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.0.1"))
        .withNetwork(NETWORK);
    private static final ToxiproxyContainer TOXIPROXY = new ToxiproxyContainer("shopify/toxiproxy:2.1.0")
        .withNetwork(NETWORK);

    private KafkaProducerService kafkaProducerService;
    private KafkaProducer<String, CouponIssueRequestAvroModel> kafkaProducer;
    private ClientAndServer mockSchemaRegistry;
    private ToxiproxyContainer.ContainerProxy kafkaProxy;
    private AdminClient adminClient;
    private KafkaAdminClient myAdminClient;

    @BeforeEach
    void setUp() {
        KAFKA.start();
        TOXIPROXY.start();

        kafkaProxy = TOXIPROXY.getProxy(KAFKA, 9093);

        // Start mock Schema Registry
        mockSchemaRegistry = ClientAndServer.startClientAndServer(8081);
        // 스키마 버전 조회시 schema 리턴.
        mockSchemaRegistry.when(
            request()
                .withMethod("GET")
                .withPath("/subjects/.*/versions/latest")
        ).respond(
            response()
                .withStatusCode(200)
                .withBody("{\"schema\": \"{\\\"type\\\": \\\"record\\\", \\\"name\\\": \\\"CouponIssueRequestAvroModel\\\", \\\"fields\\\": [{\\\"name\\\": \\\"issueId\\\", \\\"type\\\": \\\"long\\\"}, {\\\"name\\\": \\\"callerId\\\", \\\"type\\\": \\\"string\\\"}, {\\\"name\\\": \\\"couponId\\\", \\\"type\\\": \\\"string\\\"}, {\\\"name\\\": \\\"amount\\\", \\\"type\\\": \\\"long\\\"}, {\\\"name\\\": \\\"createdAt\\\", \\\"type\\\": {\\\"type\\\": \\\"long\\\", \\\"logicalType\\\": \\\"timestamp-millis\\\"}}]}\"}")
        );
        mockSchemaRegistry.when(
            request()
                .withMethod("POST")
                .withPath("/subjects/.*/versions")
        ).respond(
            response()
                .withStatusCode(200)
                .withBody("{\"id\": 1}")
        );
        // Create and configure KafkaConfigData
        KafkaConfigData kafkaConfigData = new KafkaConfigData();
        kafkaConfigData.setBootstrapServers(kafkaProxy.getContainerIpAddress() + ":" + kafkaProxy.getProxyPort());
        kafkaConfigData.setSchemaRegistryUrlKey("schema.registry.url");
        kafkaConfigData.setSchemaRegistryUrl("http://localhost:8081");
        kafkaConfigData.setTopicName("coupon-issue-topic");
        kafkaConfigData.setTopicNamesToCreate(Arrays.asList("coupon-issue-topic"));
        kafkaConfigData.setNumOfPartitions(1);
        kafkaConfigData.setReplicationFactor((short) 1);

        // Create and configure KafkaProducerConfigData
        KafkaProducerConfigData kafkaProducerConfigData = new KafkaProducerConfigData();
        kafkaProducerConfigData.setKeySerializerClass("org.apache.kafka.common.serialization.StringSerializer");
        kafkaProducerConfigData.setValueSerializerClass("io.confluent.kafka.serializers.KafkaAvroSerializer");
        kafkaProducerConfigData.setBatchSize(16384);
        kafkaProducerConfigData.setBatchSizeBoostFactor(100);
        kafkaProducerConfigData.setLingerMs(5);
        kafkaProducerConfigData.setCompressionType("snappy");
        kafkaProducerConfigData.setAcks("all");
        kafkaProducerConfigData.setRequestTimeoutMs(60000);
        kafkaProducerConfigData.setRetryCount(5);

        // Create KafkaAdminConfig and get AdminClient
        KafkaAdminConfig kafkaAdminConfig = new KafkaAdminConfig(kafkaConfigData);
        adminClient = kafkaAdminConfig.adminClient();

        myAdminClient = new KafkaAdminClient(kafkaConfigData, adminClient);
        myAdminClient.createTopics();
        // Create KafkaProducerConfig
        KafkaProducerConfig<String, CouponIssueRequestAvroModel> kafkaProducerConfig =
            new KafkaProducerConfig<>(myAdminClient, kafkaConfigData, kafkaProducerConfigData);

        // Create KafkaProducer
        kafkaProducer = new KafkaProducer<>(myAdminClient, kafkaProducerConfig.kafkaTemplate());
        kafkaProducerService = new KafkaProducerService(kafkaProducer);

        // Kafka 브로커가 준비될 때까지 대기
        await()
            .atMost(60, TimeUnit.SECONDS)
            .until(() -> {
                try (AdminClient client = AdminClient.create(
                    Map.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProxy.getContainerIpAddress() + ":" + kafkaProxy.getProxyPort())
                )) {
                    Set<String> topics = client.listTopics().names().get(10, TimeUnit.SECONDS);
                    return topics.contains("coupon-issue-topic");
                } catch (Exception e) {
                    return false;
                }
            });
    }

    @Test
    void testSendCouponIssueRequest_AckSuccess() {
        // Arrange
        IssueRequestDto issueRequestDto = new IssueRequestDto("coupon123", 1000000L);

        // Simulate network failure using Toxiproxy
        kafkaProxy.setConnectionCut(false);

        // Act
        Mono<Boolean> result = kafkaProducerService.sendCouponIssueRequest(issueRequestDto);

        // Assert
        StepVerifier.create(result)
            .expectNext(true)
            .verifyComplete();
    }

//    @Test
//    void testSendCouponIssueRequest_AckFailure() throws IOException {
//        // Arrange
//        IssueRequestDto issueRequestDto = new IssueRequestDto("coupon123", 1000000L);
//
//        // Simulate ACK failure using Toxiproxy
//        // 브로커의 응답만 차단 (아웃바운드 트래픽)
//        kafkaProxy.toxics().bandwidth("SLOW_RESPONSE", ToxicDirection.DOWNSTREAM, 0L);
//
//        // Act
//        Mono<Boolean> result = kafkaProducerService.sendCouponIssueRequest(issueRequestDto);
//
//        // Assert
//        StepVerifier.create(result)
//            .expectNext(false)
//            .verifyComplete();
//
//        // Clean up
//        kafkaProxy.toxics().get("SLOW_RESPONSE").remove();
//    }

    @AfterEach
    void tearDown() {
        adminClient.close();

        // Kafka 컨테이너 종료
        KAFKA.stop();
        awaitContainerStopped(KAFKA);

        // Toxiproxy 컨테이너 종료
        TOXIPROXY.stop();
        awaitContainerStopped(TOXIPROXY);

        mockSchemaRegistry.stop();
    }

    private void awaitContainerStopped(Container<?> container) {
        Awaitility.await()
            .atMost(Duration.ofSeconds(30))
            .pollInterval(Duration.ofSeconds(1))
            .until(() -> {
                try {
                    container.execInContainer("ps", "-ef");
                    return false; // 컨테이너가 아직 실행 중이면 false 반환
                } catch (Exception e) {
                    return true; // 컨테이너가 종료되면 예외가 발생하고 true 반환
                }
            });
    }
}