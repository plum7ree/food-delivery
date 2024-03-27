package com.example.callapplication.service.listener.kafka;

import com.example.callapplication.service.CallAndPaymentSaga;
import com.example.callconfigdata.CallServiceConfigData;
import com.example.calldomain.data.mapper.DataMapper;
import com.example.kafka.avro.model.PaymentResponseAvroModel;
import com.example.kafka.avro.model.PaymentStatus;
import com.example.kafkaconsumer.KafkaConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Conversions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final Conversions.DecimalConversion decimalConversion = new Conversions.DecimalConversion();
    private final CallServiceConfigData callServiceConfigData;
    private final CallAndPaymentSaga callAndPaymentSaga;
    private final DataMapper dataMapper;

    @Value("${kafka-consumer-config.payment-consumer-group-id}")
    private String consumerGroupId;

    @EventListener
    public void OnAppStarted(ApplicationStartedEvent event) {
        log.info("on app started!");
        log.info("consumer group id: {}", consumerGroupId);
        kafkaListenerEndpointRegistry.getListenerContainer(consumerGroupId).start();
    }

    @Override
    @KafkaListener(id = "${kafka-consumer-config.payment-consumer-group-id}",
            topics = "${call-service.payment-response-topic-name}")
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        messages.forEach(responseAvroModel -> {
            log.info("payment response topic received");
            if (PaymentStatus.COMPLETED == responseAvroModel.getPaymentStatus()) {
                var event = callAndPaymentSaga.process(
                        dataMapper.paymentResponseAvroToPaymentResponseDto(responseAvroModel));
                log.info(String.valueOf(event));
                event.fire();
            }
        });

    }
}
