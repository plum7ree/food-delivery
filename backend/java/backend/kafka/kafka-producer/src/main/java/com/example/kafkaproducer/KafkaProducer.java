package com.example.kafkaproducer;

import com.example.kafka.admin.client.KafkaAdminClient;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/**
 * To use this in a service A,
 * 1. import dependency into pom.xml
 * 2. add the package into A's main java file's @ComponentScan
 *
 * @param <K>
 * @param <V>
 */
@Component
@RequiredArgsConstructor
public class KafkaProducer<K, V> {
    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);


    private final KafkaAdminClient kafkaAdminClient;
    private final KafkaTemplate<K, V> kafkaTemplate;

    public void send(String topicName, K key, V message) {
        kafkaTemplate.send(topicName, key, message);
    }

    /**
     * spring kafka 3.2 에서는 onSuccess, onFailure 지원하지 않음
     * 'SendResult' class is executed when the message is successfully written/replicated to the partition
     * https://stackoverflow.com/questions/76954327/spring-boot-3-with-kafka-acknowledge-callback-not-working
     *
     * @param topicName Kafka 토픽 이름
     * @param key       메시지 키
     * @param message   메시지 내용
     * @param callback  ack 수신 후 실행될 콜백 함수
     *                                                                                      TODO OOM 방지 backpressure 기능 추가.
     */
    public void sendAndRunCallback(String topicName, K key, V message,
                                   BiConsumer<RecordMetadata, Exception> callback) {
        try {
            ProducerRecord<K, V> record = new ProducerRecord<>(topicName, key, message);

//        log.info("sending message");
            CompletableFuture<SendResult<K, V>> future = kafkaTemplate.send(record);
            future.whenComplete((result, exception) -> {
                if (result != null) {
                    // 성공적으로 ack를 받았을 때
                    RecordMetadata metadata = result.getRecordMetadata();
//                log.info("got ack");
                    callback.accept(metadata, null);
                } else if (exception != null) {
                    // 에러 발생 시
                    if (exception instanceof TimeoutException) {
                        callback.accept(null, new Exception("Timeout exception!", exception));
                        return;
                    }
                    callback.accept(null, new Exception("Failed to send message", exception));
                }
            });
        } catch (KafkaException e) {
            log.error("Error on kafka producer with key: {}, message: {} and exception: {}", key, message,
                e.getMessage());
        }
    }

    @EventListener
    public void OnAppStarted(ApplicationStartedEvent event) {
        log.info("Kafka Producer createTopics!");
        kafkaAdminClient.createTopics();
    }
}
