package com.example.kafkaproducer;

import com.example.kafka.admin.client.KafkaAdminClient;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.errors.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
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

    public void send(String topicName, K key, V message,
                     BiConsumer<SendResult<K, V>, Throwable> callback) {
        CompletableFuture<SendResult<K, V>> future = kafkaTemplate.send(topicName, key, message);
        future.whenComplete(callback);
    }


    /**
     * 메시지를 보내고 ack를 받았을 때 콜백을 실행하는 함수
     *
     * @param topicName Kafka 토픽 이름
     * @param key       메시지 키
     * @param message   메시지 내용
     * @param callback  ack 수신 후 실행될 콜백 함수
     *                  TODO OOM 방지 backpressure 기능 추가.
     */
    public void sendAndRunCallbackOnAck(String topicName, K key, V message,
                                        BiConsumer<RecordMetadata, Exception> callback) {
        ProducerRecord<K, V> record = new ProducerRecord<>(topicName, key, message);

        log.info("sending message");
        CompletableFuture<SendResult<K, V>> future = kafkaTemplate.send(record);

        future.whenComplete((result, exception) -> {
            if (exception == null) {
                // 성공적으로 ack를 받았을 때
                RecordMetadata metadata = result.getRecordMetadata();
                log.info("got ack");
                callback.accept(metadata, null);
            } else {
                // 에러 발생 시
                if (exception instanceof TimeoutException) {
                    callback.accept(null, new Exception("Timeout exception!", exception));
                    return;
                }
                callback.accept(null, new Exception("Failed to send message", exception));
            }
        });
    }
    @EventListener
    public void OnAppStarted(ApplicationStartedEvent event) {
        log.info("Kafka Producer createTopics!");
        kafkaAdminClient.createTopics();
    }
}
