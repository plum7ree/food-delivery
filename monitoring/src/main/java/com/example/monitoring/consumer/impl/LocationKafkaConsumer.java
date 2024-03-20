package com.example.monitoring.consumer.impl;

import com.example.driver.data.dto.LocationDto;
import com.example.kafka.admin.client.KafkaAdminClient;
import com.example.kafka.config.data.KafkaConfigData;
import com.example.locationredis.consumer.KafkaConsumer;
import com.example.monitoring.service.LocationService;
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

import java.util.List;


@Service
@RequiredArgsConstructor
public class LocationKafkaConsumer implements KafkaConsumer<LocationAvroModel> {
    private static final Logger LOG = LoggerFactory.getLogger(LocationAvroModel.class);

    // @ComponentScan needed!
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
    // @ComponentScan needed!
    private final KafkaAdminClient kafkaAdminClient;
    // @ComponentScan needed!
    private final KafkaConfigData kafkaConfig;

    private final LocationService locationService;


    @EventListener
    public void onAppStarted(ApplicationStartedEvent event) {
//        kafkaAdminClient.checkTopicsCreated();
        LOG.info("Topics with name {} is ready for operations!", kafkaConfig.getTopicNamesToCreate().toArray());
        kafkaListenerEndpointRegistry.getListenerContainer("monitoringServiceKafkaConsumerForLocation").start();
    }

    // Kafka Listener consumes "batch" of message.
    // batch size: max.poll.records
    // If wanna disable, ContainerProperties.setBatchListener(false)
    @Override
    @KafkaListener(topics = "${kafka-config.topic-name}", autoStartup = "false")
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

            messages.stream()
            .map(avro -> new LocationDto()
                            .builder()
                            .lat(avro.getCoord().getLat())
                            .lon(avro.getCoord().getLat())
                            .edgeId(String.valueOf(avro.getEdgeId()))
                            .oldEdgeId(String.valueOf(avro.getOldEdgeId()))
                            .driverId(String.valueOf(avro.getDriverId()))
                            .build())
            .forEach(locationService::sendLocation);
    }

}
