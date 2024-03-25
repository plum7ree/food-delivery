package com.example.kafkaconsumer;

import org.apache.avro.specific.SpecificRecordBase;

import java.util.List;

/**
 * To use this in a service A,
 * 1. import dependency into pom.xml
 * 2. add the package into A's main java file's @ComponentScan
 */
public interface KafkaConsumer<T extends SpecificRecordBase> {
    void receive(List<T> messages, List<String> keys, List<Integer> partitions, List<Long> offsets);
}
