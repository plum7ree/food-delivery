//package com.example.eatsorderapplication.config;
//
//
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.apache.kafka.common.serialization.StringSerializer;
//import io.confluent.kafka.serializers.KafkaAvroSerializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.core.DefaultKafkaProducerFactory;
//import org.springframework.kafka.core.KafkaTemplate;
//import org.springframework.kafka.core.ProducerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Configuration
//public class KafkaProducerConfig {
//
//    @Value("${kafka-producer-config.key-serializer-class}")
//    private String keySerializer;
//
//    @Value("${kafka-producer-config.value-serializer-class}")
//    private String valueSerializer;
//
//    @Value("${kafka-producer-config.compression-type}")
//    private String compressionType;
//
//    @Value("${kafka-producer-config.acks}")
//    private String acks;
//
//    @Value("${kafka-producer-config.batch-size}")
//    private int batchSize;
//
//    @Value("${kafka-producer-config.linger-ms}")
//    private int lingerMs;
//
//    @Value("${kafka-producer-config.request-timeout-ms}")
//    private int requestTimeoutMs;
//
//    @Value("${kafka-producer-config.retry-count}")
//    private int retryCount;
//
//    @Bean
//    public Map<String, Object> producerConfigs() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySerializer);
//        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSerializer);
//        props.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, compressionType);
//        props.put(ProducerConfig.ACKS_CONFIG, acks);
//        props.put(ProducerConfig.BATCH_SIZE_CONFIG, batchSize);
//        props.put(ProducerConfig.LINGER_MS_CONFIG, lingerMs);
//        props.put(ProducerConfig.REQUEST_TIMEOUT_MS_CONFIG, requestTimeoutMs);
//        props.put(ProducerConfig.RETRIES_CONFIG, retryCount);
//        return props;
//    }
//
//    @Bean
//    public ProducerFactory<String, Object> producerFactory() {
//        return new DefaultKafkaProducerFactory<>(producerConfigs());
//    }
//
//    @Bean
//    public KafkaTemplate<String, Object> kafkaTemplate() {
//        return new KafkaTemplate<>(producerFactory());
//    }
//}
