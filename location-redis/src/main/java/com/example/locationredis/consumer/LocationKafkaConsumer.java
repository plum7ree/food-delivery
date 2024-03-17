package com.example.locationredis.consumer;

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

import java.util.List;


import com.example.analytics.service.business.KafkaConsumer;
import com.example.analytics.service.dataaccess.entity.AnalyticsEntity;
import com.example.analytics.service.dataaccess.repository.AnalyticsRepository;
import com.example.analytics.service.transformer.AvroToDbEntityModelTransformer;
import com.example.config.KafkaConfigData;
import com.example.kafka.admin.client.KafkaAdminClient;
import com.example.kafka.avro.model.TwitterAnalyticsAvroModel;
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

import java.util.List;

@Service
public class LocationKafkaConsumer implements KafkaConsumer<LocationAvroModel> {

    private static final Logger LOG = LoggerFactory.getLogger(AnalyticsKafkaConsumer.class);

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final KafkaAdminClient kafkaAdminClient;

    private final KafkaConfigData kafkaConfig;

    private final AvroToDbEntityModelTransformer avroToDbEntityModelTransformer;

    private final AnalyticsRepository analyticsRepository;


    public LocationKafkaConsumer(KafkaListenerEndpointRegistry registry,
                                  KafkaAdminClient adminClient,
                                  KafkaConfigData config,
                                  AvroToDbEntityModelTransformer transformer,
                                  AnalyticsRepository repository) {
        this.kafkaListenerEndpointRegistry = registry;
        this.kafkaAdminClient = adminClient;
        this.kafkaConfig = config;
        this.avroToDbEntityModelTransformer = transformer;
        this.analyticsRepository = repository;
    }

    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
        kafkaAdminClient.checkTopicsCreated();
        LOG.info("Topics with name {} is ready for operations!", kafkaConfig.getTopicNamesToCreate().toArray());
        kafkaListenerEndpointRegistry.getListenerContainer("twitterAnalyticsTopicListener").start();
    }

    @Override
    @KafkaListener(id = "twitterAnalyticsTopicListener", topics = "${kafka-config.topic-name}", autoStartup = "false")
    public void receive(@Payload List<TwitterAnalyticsAvroModel> messages,
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
        List<AnalyticsEntity> twitterAnalyticsEntities = avroToDbEntityModelTransformer.getEntityModel(messages);
        analyticsRepository.batchPersist(twitterAnalyticsEntities);
        LOG.info("{} number of messaged send to database", twitterAnalyticsEntities.size());
    }

}
