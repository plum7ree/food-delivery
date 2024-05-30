package com.example.paymentservice.service.listener.kafka;

import com.example.commondata.domain.aggregate.UberPayment;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.PaymentId;
import com.example.commondata.domain.aggregate.valueobject.CallerId;
import com.example.kafka.avro.model.PaymentRequestAvroModel;
import com.example.kafka.avro.model.PaymentResponseAvroModel;
import com.example.kafka.avro.model.PaymentStatus;
import com.example.kafkaconsumer.KafkaConsumer;
import com.example.kafkaproducer.KafkaProducer;
import com.example.paymentservice.config.CallServiceConfigData;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Conversions.DecimalConversion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UberPaymentRequestKafkaListener implements KafkaConsumer<PaymentRequestAvroModel> {
    private static final Logger log = LoggerFactory.getLogger(UberPaymentRequestKafkaListener.class);

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;


    private final KafkaProducer<String, PaymentResponseAvroModel> paymentReponseKafkaProducer;
    private final DecimalConversion decimalConversion = new DecimalConversion();

    // @ConfigurationProperties(prefix = "payment-service")
    private final CallServiceConfigData paymentServiceConfigData;

    @Value("${kafka-consumer-config.consumer-group-id}")
    private String consumerGroupId;

    @EventListener
    public void OnAppStarted(ApplicationStartedEvent event) {
        log.info("on app started!");
        log.info("consumer group id: {}", consumerGroupId);
        kafkaListenerEndpointRegistry.getListenerContainer(consumerGroupId).start();
    }


    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}",
            topics = "${call-service.payment-request-topic-name}")
    public void receive(@Payload List<PaymentRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        messages.forEach(avroModel -> {
            log.info("payment request receive called.");
            //TODO save into DB
            var paymentDto = UberPayment.builder()
                    .paymentId(new PaymentId(UUID.fromString(avroModel.getId().toString())))
                    .userId(new CallerId(UUID.fromString(avroModel.getUserId().toString())))
                    .callId(new CallId(UUID.fromString(avroModel.getCallId().toString())))
                    .price(new Money(decimalConversion.fromBytes(avroModel.getPrice(),
                            avroModel.getSchema().getField("price").schema(),
                            avroModel.getSchema().getField("price").schema().getLogicalType())))
                    .paymentStatus(avroModel.getPaymentStatus())
                    .build();

            // validate,
            // generate paymentSuccess, paymentFail event like CallPaidEvent
            var now = ZonedDateTime.now(ZoneId.of("UTC"));

            var response = PaymentResponseAvroModel.newBuilder()
                    .setPaymentId(paymentDto.getId().getValue().toString())
                    .setPaymentStatus(PaymentStatus.COMPLETED)
                    .setCallId(paymentDto.getCallId().getValue().toString())
                    .setId(paymentDto.getId().getValue().toString())
                    .setPrice(decimalConversion.toBytes(paymentDto.getPrice().getAmount(),
                            PaymentResponseAvroModel.getClassSchema().getField("price").schema(),
                            PaymentResponseAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
                    .setSagaId("")
                    .setUserId(paymentDto.getCallerId().getValue().toString())
                    .setFailureMessages("")
                    .setCreatedAt(now.toInstant())
                    .build();

            log.info("payment response producer send: {}", paymentServiceConfigData.getPaymentResponseTopicName());
            paymentReponseKafkaProducer.send(paymentServiceConfigData.getPaymentResponseTopicName(),
                    avroModel.getCallId().toString(),
                    response);
        });
    }

}

