//package com.example.restaurantapprovalservice;
//
//import com.example.commondata.domain.aggregate.Payment;
//import com.example.commondata.domain.aggregate.valueobject.OrderId;
//import com.example.commondata.domain.aggregate.valueobject.Money;
//import com.example.commondata.domain.aggregate.valueobject.PaymentId;
//import com.example.commondata.domain.aggregate.valueobject.CallerId;
//import com.example.kafka.avro.model.*;
//import com.example.kafkaproducer.KafkaProducer;
//import com.example.restaurantapprovalservice.config.CallServiceConfigData;
//import com.example.restaurantapprovalservice.service.listener.kafka.PaymentRequestKafkaListener;
//import org.apache.avro.Conversions.DecimalConversion;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
//
//import java.math.BigDecimal;
//import java.time.Instant;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.util.Collections;
//import java.util.UUID;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PaymentRequestKafkaListenerTest {
//
//    @Mock
//    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
//
//    @Mock
//    private KafkaProducer<String, ResponseAvroModel> paymentReponseKafkaProducer;
//
//    @Spy
//    private DecimalConversion decimalConversion = new DecimalConversion();
//
//    @Mock
//    private CallServiceConfigData paymentServiceConfigData;
//
//    @InjectMocks
//    private PaymentRequestKafkaListener paymentRequestKafkaListener;
//
//    @Test
//    void shouldProcessPaymentRequestAndSendResponse() {
//
//        var paymentRequestId = UUID.randomUUID().toString();
//        var sagaId = UUID.randomUUID().toString();
//        var userId = UUID.randomUUID().toString();
//        var restaurantId = UUID.randomUUID().toString();
//        var callId = UUID.randomUUID().toString();
//        long currentTimestamp = System.currentTimeMillis();
//        // Given
//        var paymentRequestAvroModel = RequestAvroModel.newBuilder()
//                .setId(paymentRequestId)
//                .setSagaId(sagaId)
//                .setCallId(callId)
//                .setCallerId(userId)
//                .setCalleeId(restaurantId)
//                .setPrice(decimalConversion.toBytes(new BigDecimal("10.00"), PaymentRequestAvroModel.getClassSchema().getField("price").schema(), PaymentRequestAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
//                .setStatus(Status.COMPLETED)
//                .setCreatedAt(Instant.ofEpochSecond(currentTimestamp))
//                .build();
//
//        var paymentEntity = Payment.builder()
//                .id(new PaymentId(UUID.fromString(paymentRequestAvroModel.getId().toString())))
//                .callerId(new CallerId(UUID.fromString(paymentRequestAvroModel.getCallerId().toString())))
//                .calleeId(new CallerId(UUID.fromString(paymentRequestAvroModel.getCalleeId().toString())))
//                .orderId(new OrderId(UUID.fromString(paymentRequestAvroModel.getCallId().toString())))
//                .price(new Money(decimalConversion.fromBytes(paymentRequestAvroModel.getPrice(), paymentRequestAvroModel.getSchema().getField("price").schema(), paymentRequestAvroModel.getSchema().getField("price").schema().getLogicalType())))
//                .status(paymentRequestAvroModel.getStatus())
//                .build();
//
//        // receive 내부의 created at 과 일치하게 mock 해야함.
//        ZonedDateTime now = ZonedDateTime.of(2023, 5, 1, 12, 0, 0, 0, ZoneId.of("UTC"));
//        // now() 는 static 이기 때문에 mockStatic 으로 해야함.
//        var mockedStatic = mockStatic(ZonedDateTime.class);
//        mockedStatic.when(() -> ZonedDateTime.now(ZoneId.of("UTC"))).thenReturn(now);
//
//
//        var expectedPaymentResponse = ResponseAvroModel.newBuilder()
//                .setPaymentId(paymentEntity.getId().getValue().toString())
//                .setStatus(Status.COMPLETED)
//                .setCallId(paymentEntity.getOrderId().getValue().toString())
//                .setId(paymentEntity.getId().getValue().toString())
//                .setPrice(decimalConversion.toBytes(paymentEntity.getPrice().getAmount(),
//                        ResponseAvroModel.getClassSchema().getField("price").schema(),
//                        ResponseAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
//                .setSagaId("")
//                .setCallerId(paymentEntity.getCallerId().getValue().toString())
//                .setCalleeId(paymentEntity.getCalleeId().getValue().toString())
//                .setFailureMessages("")
//                .setCreatedAt(now.toInstant())
//                .build();
//
//        when(paymentServiceConfigData.getPaymentResponseTopicName())
//                .thenReturn("payment-response-topic");
//
//        // When
//        // receive 시뮬레이션
//        paymentRequestKafkaListener.receive(
//                Collections.singletonList(paymentRequestAvroModel),
//                Collections.singletonList("key"),
//                Collections.singletonList(0),
//                Collections.singletonList(0L));
//
//        // Then
//        // paymentReponseKafkaProducer 가 send 함수를 호출해야한다.
//        // 각 파라미터는 이럴 것이다. 테스트.
//        verify(paymentReponseKafkaProducer).send(
//                eq("payment-response-topic"),
//                eq(paymentRequestAvroModel.getCallId().toString()),
//                eq(expectedPaymentResponse));
//
//
//
//    }
//}