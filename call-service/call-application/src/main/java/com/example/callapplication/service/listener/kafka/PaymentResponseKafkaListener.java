package com.example.callapplication.service.listener.kafka;

import com.example.callapplication.service.CallAndPaymentSaga;
import com.example.calldomain.data.mapper.DataMapper;
import com.example.kafka.avro.model.PaymentResponseAvroModel;
import com.example.kafka.avro.model.PaymentStatus;
import com.example.kafkaconsumer.KafkaConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class PaymentResponseKafkaListener implements KafkaConsumer<PaymentResponseAvroModel> {

    private final CallAndPaymentSaga callAndPaymentSaga;
    private final DataMapper dataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id.payment}",
                topics = "${kafka-consumer-config.topic-name.payment-response-topic}")
    public void receive(@Payload List<PaymentResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        messages.forEach(responseAvroModel -> {
            if (PaymentStatus.COMPLETED == responseAvroModel.getPaymentStatus()) {
                var event = callAndPaymentSaga.process(
                        dataMapper.paymentResponseAvroToPaymentResponseDto(responseAvroModel));
                event.fire();
            }
        });

    }
}
