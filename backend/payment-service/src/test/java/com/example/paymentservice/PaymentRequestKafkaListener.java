package com.example.paymentservice;

import com.example.commondata.domain.aggregate.Payment;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.PaymentId;
import com.example.commondata.domain.aggregate.valueobject.UserId;
import com.example.kafka.avro.model.PaymentRequestAvroModel;
import com.example.kafka.avro.model.PaymentResponseAvroModel;
import com.example.kafka.avro.model.PaymentStatus;
import com.example.kafkaproducer.KafkaProducer;
import com.example.paymentservice.config.CallServiceConfigData;
import com.example.paymentservice.service.listener.kafka.PaymentRequestKafkaListener;
import org.apache.avro.Conversions.DecimalConversion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentRequestKafkaListenerTest {

    @Mock
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Mock
    private KafkaProducer<String, PaymentResponseAvroModel> paymentReponseKafkaProducer;

    @Spy
    private DecimalConversion decimalConversion = new DecimalConversion();

    @Mock
    private CallServiceConfigData paymentServiceConfigData;

    @InjectMocks
    private PaymentRequestKafkaListener paymentRequestKafkaListener;

    @Test
    void shouldProcessPaymentRequestAndSendResponse() {

        var paymentRequestId = UUID.randomUUID().toString();
        var sagaId = UUID.randomUUID().toString();
        var userId = UUID.randomUUID().toString();
        var callId = UUID.randomUUID().toString();
        long currentTimestamp = System.currentTimeMillis();
        // Given
        var paymentRequestAvroModel = PaymentRequestAvroModel.newBuilder()
                .setId(paymentRequestId)
                .setSagaId(sagaId)
                .setUserId(userId)
                .setCallId(callId)
                .setPrice(decimalConversion.toBytes(new BigDecimal("10.00"), PaymentRequestAvroModel.getClassSchema().getField("price").schema(), PaymentRequestAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
                .setPaymentStatus(PaymentStatus.COMPLETED)
                .setCreatedAt(Instant.ofEpochSecond(currentTimestamp))
                .build();

        var paymentDto = Payment.builder()
                .paymentId(new PaymentId(UUID.fromString(paymentRequestAvroModel.getId().toString())))
                .userId(new UserId(UUID.fromString(paymentRequestAvroModel.getUserId().toString())))
                .callId(new CallId(UUID.fromString(paymentRequestAvroModel.getCallId().toString())))
                .price(new Money(decimalConversion.fromBytes(paymentRequestAvroModel.getPrice(), paymentRequestAvroModel.getSchema().getField("price").schema(), paymentRequestAvroModel.getSchema().getField("price").schema().getLogicalType())))
                .paymentStatus(paymentRequestAvroModel.getPaymentStatus())
                .build();

        var now = ZonedDateTime.now(ZoneId.of("UTC"));

        var expectedPaymentResponse = PaymentResponseAvroModel.newBuilder()
                .setPaymentId(paymentDto.getId().getValue().toString())
                .setPaymentStatus(PaymentStatus.COMPLETED)
                .setCallId(paymentDto.getCallId().getValue().toString())
                .setId(paymentDto.getId().getValue().toString())
                .setPrice(decimalConversion.toBytes(paymentDto.getPrice().getAmount(), PaymentResponseAvroModel.getClassSchema().getField("price").schema(), PaymentResponseAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
                .setSagaId("")
                .setUserId(paymentDto.getUserId().getValue().toString())
                .setFailureMessages("")
                .setCreatedAt(now.toInstant())
                .build();

        when(paymentServiceConfigData.getPaymentResponseTopicName())
                .thenReturn("payment-response-topic");

        // When
        paymentRequestKafkaListener.receive(
                Collections.singletonList(paymentRequestAvroModel),
                Collections.singletonList("key"),
                Collections.singletonList(0),
                Collections.singletonList(0L));

        // Then
        // paymentReponseKafkaProducer 가 send 함수를 호출할 것이다.
        // 각 파라미터는 이럴 것이다. 테스트.
        verify(paymentReponseKafkaProducer).send(
                eq("payment-response-topic"),
                eq(paymentRequestAvroModel.getCallId().toString()),
                eq(expectedPaymentResponse));



    }
}