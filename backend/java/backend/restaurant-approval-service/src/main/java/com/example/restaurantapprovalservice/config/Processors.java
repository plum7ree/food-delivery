package com.example.restaurantapprovalservice.config;


import com.example.commondata.domain.events.notification.NotificationEvent;
import com.example.commondata.message.MessageConverter;
import com.example.kafka.avro.model.NotificationAvroModel;
import com.example.kafka.avro.model.RequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;

import java.util.function.Function;


@Slf4j
@Configuration
public class Processors {


    public Processors() {

    }


    @Bean
    public Function<Flux<Message<RequestAvroModel>>, Flux<Message<RequestAvroModel>>> processor() {
        return flux -> flux.map(MessageConverter::toRecord)
            .doOnNext(r -> r.acknowledgement().acknowledge())
            .doOnNext(r -> log.info("inventory service received {}", r.message()))
            .flatMap(r -> Flux.just(MessageBuilder.withPayload(r.message())
                .setHeader(KafkaHeaders.PARTITION, 0)
                .build()
            ));
    }


}