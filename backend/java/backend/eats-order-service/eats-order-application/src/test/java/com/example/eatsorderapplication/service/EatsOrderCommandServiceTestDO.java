package com.example.eatsorderapplication.service;

import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdomain.data.aggregate.OrderDomainObject;
import com.example.eatsorderdomain.data.dto.CreateOrderCommandDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.eatsorderdomain.data.mapper.DataMapper;
import com.example.kafka.avro.model.PaymentRequestAvroModel;
import com.example.kafka.avro.model.RequestAvroModel;
import com.example.kafka.avro.model.Status;
import com.example.kafkaproducer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EatsOrderCommandServiceTestDO {


    @InjectMocks
    private EatsOrderCommandService eatsOrderCommandService;

    @Mock
    private EatsOrderServiceConfigData eatsOrderServiceConfigData;

    @Mock
    private KafkaProducer<String, RequestAvroModel> kafkaProducer;

    @Captor
    private ArgumentCaptor<String> topicNameCaptor;

    @Captor
    private ArgumentCaptor<String> keyCaptor;

    @Captor
    private ArgumentCaptor<RequestAvroModel> messageCaptor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testPublish() {
        // Given
        UUID userId = UUID.fromString("d290f1ee-6c54-4b01-90e6-d701748f0851");
        UUID driverId = UUID.fromString("c240a1ee-6c54-4b01-90e6-d701748f0852");
        BigDecimal price = new BigDecimal("100.50");
        var street = "123 Main St";
        var postalCode = "12345";
        var city = "City";
        Address address = Address.builder()
                .street(street)
                .postalCode(postalCode)
                .city(city)
                .build();
        var createEatsOrderCommandDto = CreateOrderCommandDto.builder()
                .address(address)
            .calleeId(driverId)
                .price(price)
            .callerId(userId)
                .payment(null)
                .build();


        var callId = new OrderId(UUID.randomUUID());
        var nowMock = ZonedDateTime.now(ZoneId.of("UTC"));
        var callerId = new CallerId(userId);
        var calleeId = new CalleeId(driverId);
        var money = new Money(price);


        OrderDomainObject callAfterCreateOrderTransactionDO = OrderDomainObject.builder()
                .id(callId)
                .calleeId(calleeId)
                .callerId(callerId)
                .price(money)
            .trackingId(new SimpleId(UUID.randomUUID()))
            .orderStatus(OrderStatus.PENDING)
                .status(Status.PENDING)
                .build();

        CallCreatedEvent callCreatedEvent = new CallCreatedEvent(callAfterCreateOrderTransactionDO, nowMock);

        MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class);
        zonedDateTimeMockedStatic.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(nowMock);
        when(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName()).thenReturn("test-topic");
        // When
        EatsOrderResponseDto responseDto = eatsOrderCommandService.createAndPublishOrder(createEatsOrderCommandDto);

        // Then
        verify(kafkaProducer).send(topicNameCaptor.capture(), keyCaptor.capture(), messageCaptor.capture());

        assertEquals("test-topic", topicNameCaptor.getValue());
        assertEquals(callerId.getValue().toString(), messageCaptor.getValue().getCallerId());
        assertEquals(calleeId.getValue().toString(), messageCaptor.getValue().getCalleeId());
        assertEquals(DataMapper.decimalConversion.toBytes(money.getAmount(),
                        PaymentRequestAvroModel.getClassSchema().getField("price").schema(),
                        PaymentRequestAvroModel.getClassSchema().getField("price").schema().getLogicalType()), messageCaptor.getValue().getPrice());
        assertEquals("", messageCaptor.getValue().getSagaId());
//        assertEquals(nowMock.toInstant(), messageCaptor.getValue().getCreatedAt());

        assertEquals(callId.getValue().toString(), messageCaptor.getValue().getId());
        assertEquals(messageCaptor.getValue().getStatus(), Status.PENDING);
        assertEquals(callCreatedEvent.getOrderDomainObject().getId().getValue().toString(), keyCaptor.getValue());
    }
}