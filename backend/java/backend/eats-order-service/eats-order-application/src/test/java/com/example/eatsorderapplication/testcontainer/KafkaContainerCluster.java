package com.example.eatsorderapplication.testcontainer;

import lombok.SneakyThrows;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.lifecycle.Startable;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
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

    private final int brokersNum;

    private final Network network;

    private final GenericContainer<?> zookeeper;
    private final GenericContainer<?> schemaRegistry;

    private final Collection<KafkaContainer> brokers;

    public KafkaContainerCluster(String confluentPlatformVersion, int brokersNum, int internalTopicsRf) {
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
                .withEnv("ZOOKEEPER_CLIENT_PORT", String.valueOf(KafkaContainer.ZOOKEEPER_PORT));

        this.brokers =
            IntStream
                .range(0, this.brokersNum)
                .mapToObj(brokerNum -> {
                    return new KafkaContainer(
                        DockerImageName.parse("confluentinc/cp-kafka").withTag(confluentPlatformVersion)
                    )
                        .withNetwork(this.network)
                        .withNetworkAliases("broker-" + brokerNum)
                        .dependsOn(this.zookeeper)
                        .withExternalZookeeper("zookeeper:" + KafkaContainer.ZOOKEEPER_PORT)
                        .withEnv("KAFKA_BROKER_ID", brokerNum + "")
                        .withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", internalTopicsRf + "")
                        .withEnv("KAFKA_OFFSETS_TOPIC_NUM_PARTITIONS", internalTopicsRf + "")
                        .withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", internalTopicsRf + "")
                        .withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", internalTopicsRf + "")
                        .withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:9092,BROKER://0.0.0.0:9093")
                        .withEnv("KAFKA_ADVERTISED_LISTENERS", "PLAINTEXT://broker-" + brokerNum + ":9092,BROKER://broker-" + brokerNum + ":9093")
                        .withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "BROKER")
                        .withStartupTimeout(Duration.ofMinutes(1));
                })
                .collect(Collectors.toList());

        this.schemaRegistry =
            new GenericContainer<>(DockerImageName.parse("confluentinc/cp-schema-registry").withTag(confluentPlatformVersion))
                .withNetwork(network)
                .withNetworkAliases("schema-registry")
                .withExposedPorts(8081)
                .dependsOn(this.zookeeper)
                .dependsOn(this.brokers)
                .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schema-registry")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL", "zookeeper:2181")
                .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8081")
                .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", brokers.stream()
                    .map(broker -> "BROKER://" + broker.getNetworkAliases().get(0) + ":9093")
                    .collect(Collectors.joining(",")))
                .withEnv("SCHEMA_REGISTRY_DEBUG", "true")
                .waitingFor(Wait.forSuccessfulCommand("curl -f http://schema-registry:8081/subjects").withStartupTimeout(Duration.ofMinutes(2)));

        schemaRegistry.setPortBindings(List.of("8081:8081"));
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

    @Override
    @SneakyThrows
    public void start() {
        // sequential start to avoid resource contention on CI systems with weaker hardware
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


        Startables.deepStart(schemaRegistry).join();

    }

    @Override
    public void stop() {
        allContainers().parallel().forEach(GenericContainer::stop);
    }
}