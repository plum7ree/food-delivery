package com.example.locationredis.consumer.impl;

import com.example.kafka.config.data.KafkaConfigData;
import com.example.kafkaconsumer.config.KafkaConsumerConfig;
import com.example.locationredis.consumer.KafkaConsumer;
import com.example.kafka.admin.client.KafkaAdminClient;

import com.microservices.demo.kafka.avro.model.LocationAvroModel;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;
import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;


import java.util.List;


@Service
@RequiredArgsConstructor
public class LocationKafkaConsumer implements KafkaConsumer<LocationAvroModel> {
    private static final Logger LOG = LoggerFactory.getLogger(LocationAvroModel.class);

    // @ComponentScan needed
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final KafkaAdminClient kafkaAdminClient;

    private final KafkaConfigData kafkaConfig;



    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
//        kafkaAdminClient.checkTopicsCreated();
        LOG.info("Topics with name {} is ready for operations!", kafkaConfig.getTopicNamesToCreate().toArray());
        kafkaListenerEndpointRegistry.getListenerContainer("twitterAnalyticsTopicListener").start();
    }

    // Kafka Listener consumes "batch" of message.
    // batch size: max.poll.records
    // If wanna disable, ContainerProperties.setBatchListener(false)
    @Override
    @KafkaListener(id = "locationKafkaConsumer", topics = "${kafka-config.topic-name}", autoStartup = "false")
    public void receive(@Payload List<LocationAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {
        LOG.info("{} number of message received with keys {}, partitions {} and offsets {}, " +
                        "sending it to database: Thread id {}",
                messages.size(),
                keys.toString(),
                partitions.toString(),
                offsets.toString(),
                Thread.currentThread().getId());

    }

}
