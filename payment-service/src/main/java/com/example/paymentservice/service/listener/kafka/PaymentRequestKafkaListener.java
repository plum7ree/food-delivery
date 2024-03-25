package com.example.paymentservice.service.listener.kafka;

import com.example.commondata.domain.aggregate.Payment;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.UserId;
import com.example.kafka.avro.model.PaymentRequestAvroModel;
import com.example.kafka.avro.model.PaymentResponseAvroModel;
import com.example.kafka.avro.model.PaymentStatus;
import com.example.kafkaconsumer.KafkaConsumer;
import com.example.kafkaproducer.KafkaProducer;

import com.example.paymentservice.config.PaymentServiceConfigData;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Conversions.DecimalConversion;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class PaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {

    private final KafkaProducer<String, PaymentResponseAvroModel> paymentReponseKafkaProducer;
    private final DecimalConversion decimalConversion = new DecimalConversion();
    private final PaymentServiceConfigData paymentServiceConfigData;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}",
                topics = "${kafka-consumer-config.topic-name.payment-request-topic}")
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        messages.forEach(avroModel -> {

            //TODO save into DB
            var paymentDto = Payment.builder()
                    .userId(new UserId(UUID.fromString(avroModel.getUserId().toString())))
                    .callId(new CallId(UUID.fromString(avroModel.getCallId().toString())))
                    .price(new Money(decimalConversion.fromBytes(avroModel.getPrice(), avroModel.getSchema(), avroModel.getSchema().getLogicalType())))
                    .build();
            var response = PaymentResponseAvroModel.newBuilder()
                    .setPaymentId(paymentDto.getId().getValue().toString())
                    .setPaymentStatus(PaymentStatus.COMPLETED)
                    .build();
            paymentReponseKafkaProducer.send(paymentServiceConfigData.getPaymentResponseTopicName(),
                    avroModel.getCallId().toString(),
                    response);
        });
    }

}

