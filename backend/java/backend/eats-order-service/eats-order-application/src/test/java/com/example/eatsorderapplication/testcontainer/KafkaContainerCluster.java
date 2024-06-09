// ref: https://github.com/testcontainers/testcontainers-java/blob/main/examples/kafka-cluster/src/test/java/com/example/kafkacluster/KafkaContainerCluster.java
// ref: https://gist.github.com/everpeace/7a317860cab6c7fb39d5b0c13ec2543e

package com.example.eatsorderapplication.testcontainer;

import lombok.SneakyThrows;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.*;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Provides an easy way to launch a Kafka cluster with multiple brokers.
 */
public class KafkaContainerCluster implements Startable {

    public final static String schemaRegistryPort = "8081";
    private final int brokersNum;
    private final Network network;
    private final GenericContainer<?> zookeeper;
    private final Collection<KafkaContainer> brokers;
    private final String confluentPlatformVersion;
    private final ArrayList<String> brokerPorts = new ArrayList<>(List.of("19092", "29092", "39092"));
    private GenericContainer<?> schemaRegistry;

    public KafkaContainerCluster(String confluentPlatformVersion, int brokersNum, int internalTopicsRf) {
        this.confluentPlatformVersion = confluentPlatformVersion;
        if (brokersNum < 0) {
            throw new IllegalArgumentException("brokersNum '" + brokersNum + "' must be greater than 0");
        }
        if (internalTopicsRf < 0 || internalTopicsRf > brokersNum) {
            throw new IllegalArgumentException(
                "internalTopicsRf '" + internalTopicsRf + "' must be less than brokersNum and greater than 0"
            );
        }

        this.brokersNum = brokersNum;
        this.network = Network.newNetwork();

        this.zookeeper =
            new GenericContainer<>(DockerImageName.parse("confluentinc/cp-zookeeper").withTag(confluentPlatformVersion))
                .withNetwork(network)
                .withNetworkAliases("zookeeper")
                .withEnv("ZOOKEEPER_CLIENT_PORT", String.valueOf(KafkaContainer.ZOOKEEPER_PORT))
                .withExposedPorts(KafkaContainer.ZOOKEEPER_PORT);
        List<String> portBindings = new ArrayList<>(List.of(KafkaContainer.ZOOKEEPER_PORT + ":" + KafkaContainer.ZOOKEEPER_PORT));
        zookeeper.setPortBindings(portBindings);

        this.brokers =
            IntStream
                .range(0, this.brokersNum)
                .mapToObj(brokerNum -> {
                    var hostName = "kafka-broker-" + brokerNum;
                    var kafka = new KafkaContainer(
                        DockerImageName.parse("confluentinc/cp-kafka").withTag(confluentPlatformVersion)
                    )
                        .withNetwork(this.network)
                        .withNetworkAliases(hostName)
                        .dependsOn(this.zookeeper)
                        .withExternalZookeeper("zookeeper:" + KafkaContainer.ZOOKEEPER_PORT)
                        .withEnv("KAFKA_HOST_NAME", hostName)
                        .withEnv("KAFKA_BROKER_ID", brokerNum + "")
                        .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", internalTopicsRf + "")
                        .withEnv("KAFKA_OFFSETS_TOPIC_NUM_PARTITIONS", internalTopicsRf + "")
                        .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", internalTopicsRf + "")
                        .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", internalTopicsRf + "")
                        // 외부 (컨슈머,프로듀서) 접근 주소: PLAINTEXT://kafka-broker-1:9092
                        // 내부 네트워크에서 접근 주소: LISTENER_LOCAL:// ...
//                        .withEnv("KAFKA_ADVERTISED_LISTENERS",
//                            String.format("PLAINTEXT://kafka-broker-%d:%d,LISTENER_LOCAL://localhost:%s",
//                                brokerNum, KafkaContainer.KAFKA_PORT, brokerPorts.get(brokerNum)))
                        .withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "PLAINTEXT:PLAINTEXT,LISTENER_LOCAL:PLAINTEXT")
                        .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "PLAINTEXT")
                        .withEnv("KAFKA_COMPRESSION_TYPE", "producer")
                        .withStartupTimeout(Duration.ofMinutes(1));
//                    List<String> portBindings2 = new ArrayList<>(List.of(brokerPorts.get(brokerNum) + ":" + brokerPorts.get(brokerNum)));
//                    kafka.setPortBindings(portBindings2);
                    return kafka;
                })
                .collect(Collectors.toList());
//          KAFKA_ADVERTISED_LISTENERS 세팅 안하고, 아무것도 안건 들였을때, 잘됨. 로그는 다음과 같음.
//          2024-06-09 20:31:23     advertised.listeners = PLAINTEXT://localhost:58720,BROKER://add8889b5533:9092
//          2024-06-09 20:31:23     listener.security.protocol.map = BROKER:PLAINTEXT,PLAINTEXT:PLAINTEXT
//          2024-06-09 20:31:23     listeners = PLAINTEXT://0.0.0.0:9093,BROKER://0.0.0.0:9092

//          2024-06-09 20:31:23     advertised.listeners = PLAINTEXT://localhost:58720,BROKER://add8889b5533:9092
//          2024-06-09 20:31:24 [2024-06-09 11:31:24,484] INFO Registered broker 1 at path /brokers/ids/1 with addresses:
//          PLAINTEXT://localhost:58720,BROKER://add8889b5533:9092, czxid (broker epoch): 46 (kafka.zk.KafkaZkClient)
    }

    public Collection<KafkaContainer> getBrokers() {
        return this.brokers;
    }

    public String getBootstrapServers() {
        return brokers.stream().map(KafkaContainer::getBootstrapServers).collect(Collectors.joining(","));
    }

    private Stream<GenericContainer<?>> allContainers() {
        return Stream.concat(this.brokers.stream(), Stream.of(this.zookeeper));
    }
//
//    @Override
//    public void start() {
//        brokers.forEach(GenericContainer::start);
//
//        Unreliables.retryUntilTrue(
//            30,
//            TimeUnit.SECONDS,
//            () -> {
//                Container.ExecResult result =
//                    this.zookeeper.execInContainer(
//                        "sh",
//                        "-c",
//                        "zookeeper-shell zookeeper:" +
//                            KafkaContainer.ZOOKEEPER_PORT +
//                            " ls /brokers/ids | tail -n 1"
//                    );
//                String brokers = result.getStdout();
//
//                return brokers != null && brokers.split(",").length == this.brokersNum;
//            }
//        );
//
//        this.schemaRegistry =
//            new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry").withTag(this.confluentPlatformVersion))
//                .withNetwork(network)
//                .withNetworkAliases("schema-registry")
//                .withExposedPorts(8081)
////                        .withCreateContainerCmdModifier(it -> it.withHostName("schema-registry" )) // hostname
//                .dependsOn(this.zookeeper)
//                .dependsOn(this.brokers)
//                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
//                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL", "zookeeper:2181")
//                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://schema-registry:8081")
//                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka-broker-2:9092,LISTENER_LOCAL://localhost:29092")
//                .withEnv("SCHEMA_REGISTRY_DEBUG", "true");
//
//        List<String> portBindings = new ArrayList<>(List.of(schemaRegistryPort + ":" + schemaRegistryPort));
//        schemaRegistry.setPortBindings(portBindings);
//        schemaRegistry.start();
//
//
//    }


    @Override
    public void start() {
        brokers.forEach(GenericContainer::start);

        Unreliables.retryUntilTrue(
            30,
            TimeUnit.SECONDS,
            () -> {
                Container.ExecResult result =
                    this.zookeeper.execInContainer(
                        "sh",
                        "-c",
                        "zookeeper-shell zookeeper:" +
                            KafkaContainer.ZOOKEEPER_PORT +
                            " ls /brokers/ids | tail -n 1"
                    );
                String brokers = result.getStdout();

                return brokers != null && brokers.split(",").length == this.brokersNum;
            }
        );

        this.schemaRegistry =
            new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry").withTag(this.confluentPlatformVersion))
                .withNetwork(network)
                .withNetworkAliases("schema-registry")
                .withExposedPorts(8081) //
                .dependsOn(this.zookeeper)
                .dependsOn(this.brokers)
//                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL", "zookeeper:2181")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka-broker-0:9092,PLAINTEXT://kafka-broker-1:9092,PLAINTEXT://kafka-broker-2:9092")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_SECURITY_PROTOCOL", "PLAINTEXT")
                .withEnv("SCHEMA_REGISTRY_DEBUG", "true")
                .waitingFor(Wait.forHttp("/subjects").forStatusCode(200).withStartupTimeout(Duration.ofMinutes(2)));


        Startables.deepStart(schemaRegistry).join();
    }

//    @Override
//    public void stop() {
//        allContainers().parallel().forEach(GenericContainer::stop);
//    }


    @Override
    public void stop() {
        Stream.<Startable>concat(Stream.concat(this.brokers.stream(), Stream.of(this.zookeeper)), Stream.of(this.schemaRegistry))
            .parallel()
            .forEach(Startable::stop);
    }


    public String getSchemaRegistryUrl() {
        return "http://schema-registry:8081";
    }
}