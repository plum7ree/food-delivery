//// ref: https://github.com/testcontainers/testcontainers-java/blob/main/examples/kafka-cluster/src/test/java/com/example/kafkacluster/KafkaContainerCluster.java
//// ref: https://gist.github.com/everpeace/7a317860cab6c7fb39d5b0c13ec2543e
//
//package com.example.eatsorderapplication.testcontainer;
//
//import org.rnorth.ducttape.unreliables.Unreliables;
//import org.testcontainers.containers.Container;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.KafkaContainer;
//import org.testcontainers.containers.Network;
//import org.testcontainers.containers.wait.strategy.Wait;
//import org.testcontainers.lifecycle.Startable;
//import org.testcontainers.lifecycle.Startables;
//import org.testcontainers.utility.DockerImageName;
//
//import java.time.Duration;
//import java.time.temporal.ChronoUnit;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//import java.util.stream.Stream;
//
///**
// * Provides an easy way to launch a Kafka cluster with multiple brokers.
// */
//public class KafkaContainerClusterV2 implements Startable {
//
//    public final static String schemaRegistryPort = "8081";
//    private final int brokersNum;
//    private final Network network;
//    private final Collection<KafkaContainer> brokers;
//    private final String confluentPlatformVersion;
//    private final ArrayList<String> brokerPorts = new ArrayList<>(List.of("19092", "29092", "39092"));
//    private GenericContainer<?> schemaRegistry;
//
//    public KafkaContainerClusterV2(String confluentPlatformVersion, int brokersNum, int internalTopicsRf) {
//        this.confluentPlatformVersion = confluentPlatformVersion;
//        if (brokersNum < 0) {
//            throw new IllegalArgumentException("brokersNum '" + brokersNum + "' must be greater than 0");
//        }
//        if (internalTopicsRf < 0 || internalTopicsRf > brokersNum) {
//            throw new IllegalArgumentException(
//                "internalTopicsRf '" + internalTopicsRf + "' must be less than brokersNum and greater than 0"
//            );
//        }
//
//        this.brokersNum = brokersNum;
//        this.network = Network.newNetwork();
//
////        this.zookeeper =
////            new GenericContainer<>(DockerImageName.parse("confluentinc/cp-zookeeper").withTag(confluentPlatformVersion))
////                .withNetwork(network)
////                .withNetworkAliases("zookeeper")
////                .withEnv("ZOOKEEPER_CLIENT_PORT", String.valueOf(KafkaContainer.ZOOKEEPER_PORT))
////                .withExposedPorts(KafkaContainer.ZOOKEEPER_PORT);
////        List<String> portBindings = new ArrayList<>(List.of(KafkaContainer.ZOOKEEPER_PORT + ":" + KafkaContainer.ZOOKEEPER_PORT));
////        zookeeper.setPortBindings(portBindings);
//
//        this.brokers =
//            IntStream
//                .range(0, 1)
//                .mapToObj(brokerNum -> {
//                    var kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:7.4.0"))
//                    .withListener(() -> "kafka:19092")
//                    .withEmbeddedZookeeper()
//                    .withNetwork(network)
//                    .withReuse(true);
//                    return kafka;
//                })
//                .collect(Collectors.toList());
//
//        GenericContainer<?> schemaRegistry = new GenericContainer<>("confluentinc/cp-schema-registry:7.4.0")
//                    .withExposedPorts(8085)
//                    .withNetworkAliases("schemaregistry")
//                    .withNetwork(network)
//                    .withEnv("SCHEMA_REGISTRY_KAFKASTORE_BOOTSTRAP_SERVERS", "PLAINTEXT://kafka:19092")
//                    .withEnv("SCHEMA_REGISTRY_LISTENERS", "http://0.0.0.0:8085")
//                    .withEnv("SCHEMA_REGISTRY_HOST_NAME", "schemaregistry")
//                    .withEnv("SCHEMA_REGISTRY_KAFKASTORE_SECURITY_PROTOCOL", "PLAINTEXT")
//                    .waitingFor(Wait.forHttp("/subjects"))
//                    .withStartupTimeout(Duration.of(120, ChronoUnit.SECONDS));
//
//    }
//
//    public Collection<KafkaContainer> getBrokers() {
//        return this.brokers;
//    }
//
//    public String getBootstrapServers() {
//        return brokers.stream().map(KafkaContainer::getBootstrapServers).collect(Collectors.joining(","));
//    }
//
//    private Stream<GenericContainer<?>> allContainers() {
//        return this.brokers.stream();
//    }
//
//    @Override
//    public void start() {
//        brokers.forEach(GenericContainer::start);
//
//        Startables.deepStart(schemaRegistry).join();
//    }
//
////    @Override
////    public void stop() {
////        allContainers().parallel().forEach(GenericContainer::stop);
////    }
//
//
//    @Override
//    public void stop() {
//    Stream.<Startable>concat(Stream.concat(this.brokers.stream(), Stream.of(this.zookeeper)), Stream.of(this.schemaRegistry))
//        .parallel()
//        .forEach(Startable::stop);
//    }
//
//
//
//    public String getSchemaRegistryUrl() {
//        return "http://schema-registry:8081";
//    }
//}