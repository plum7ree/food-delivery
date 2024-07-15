package com.example.kafkaconsumer;

import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

/**
 * To use this in a service A,
 * 1. import dependency into pom.xml
 * 2. add the package into A's main java file's @ComponentScan
 */
public interface GeneralKafkaConsumer<T extends SpecificRecordBase> {
    default void receive(List<T> messages, List<String> keys, List<Integer> partitions, List<Long> offsets) {
    }

    default void receive(List<T> messages, List<String> keys, List<Integer> partitions, List<Long> offsets, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
    }

}
