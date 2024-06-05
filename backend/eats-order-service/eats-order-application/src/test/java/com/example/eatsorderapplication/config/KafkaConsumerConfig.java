//package com.example.eatsorderapplication.config;
//
//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import io.confluent.kafka.serializers.KafkaAvroDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.EnableKafka;
//import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
//import org.springframework.kafka.core.ConsumerFactory;
//import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@EnableKafka
//@Configuration
//public class KafkaConsumerConfig {
//
//    @Value("${kafka-consumer-config.key-deserializer}")
//    private String keyDeserializer;
//
//    @Value("${kafka-consumer-config.value-deserializer}")
//    private String valueDeserializer;
//
//    @Value("${kafka-consumer-config.auto-offset-reset}")
//    private String autoOffsetReset;
//
//    @Value("${kafka-consumer-config.specific-avro-reader}")
//    private boolean specificAvroReader;
//
//    @Value("${kafka-consumer-config.batch-listener}")
//    private boolean batchListener;
//
//    @Value("${kafka-consumer-config.session-timeout-ms}")
//    private int sessionTimeoutMs;
//
//    @Value("${kafka-consumer-config.heartbeat-interval-ms}")
//    private int heartbeatIntervalMs;
//
//    @Value("${kafka-consumer-config.max-poll-interval-ms}")
//    private int maxPollIntervalMs;
//
//    @Value("${kafka-consumer-config.max-poll-records}")
//    private int maxPollRecords;
//
//    @Value("${kafka-consumer-config.max-partition-fetch-bytes-default}")
//    private int maxPartitionFetchBytesDefault;
//
//    @Value("${kafka-consumer-config.poll-timeout-ms}")
//    private int pollTimeoutMs;
//
//    @Bean
//    public Map<String, Object> consumerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, keyDeserializer);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, valueDeserializer);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, autoOffsetReset);
//        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, sessionTimeoutMs);
//        props.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, heartbeatIntervalMs);
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs);
//        props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, maxPollRecords);
//        props.put(ConsumerConfig.MAX_PARTITION_FETCH_BYTES_CONFIG, maxPartitionFetchBytesDefault);
//        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
//        props.put("specific.avro.reader", specificAvroReader);
//        return props;
//    }
//
//    @Bean
//    public ConsumerFactory<String, Object> consumerFactory() {
//        return new DefaultKafkaConsumerFactory<>(consumerConfigs());
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
//        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory());
//        factory.setBatchListener(batchListener);
//        factory.setConcurrency(3);
//        factory.getContainerProperties().setPollTimeout(pollTimeoutMs);
//        return factory;
//    }
//}
