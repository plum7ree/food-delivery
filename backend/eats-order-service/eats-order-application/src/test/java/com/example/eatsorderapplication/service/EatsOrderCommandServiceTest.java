package com.example.eatsorderapplication.service;

import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderapplication.utils.DeepCopyUtil;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.eatsorderdomain.data.dto.CreateEatsOrderCommandDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.eatsorderdomain.data.mapper.DataMapper;
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

class EatsOrderCommandServiceTest {


    @InjectMocks
    private EatsOrderCommandService eatsOrderCommandService;

    @Mock
    private EatsOrderServiceConfigData eatsOrderServiceConfigData;
    @Mock
    private CreateCallCommandManager createCallCommandManager;

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
        var createEatsOrderCommandDto = CreateEatsOrderCommandDto.builder()
                .address(address)
                .driverId(driverId)
                .price(price)
                .userId(userId)
                .payment(null)
                .route(null)
                .build();
        Call call = Call.builder()
                .calleeId(new CalleeId(driverId))
                .callerId(new CallerId(userId))
                .price(new Money(price))
                .build();



        Call callAfterCreateCallTransaction = DeepCopyUtil.deepCopy(call);
        callAfterCreateCallTransaction.setTrackingId(new TrackingId(UUID.randomUUID()));
        callAfterCreateCallTransaction.setCallStatus(CallStatus.PENDING);
        var now = ZonedDateTime.now(ZoneId.of("UTC"));
        CallCreatedEvent callCreatedEvent = new CallCreatedEvent(callAfterCreateCallTransaction, now);
        RequestAvroModel requestAvroModel = DataMapper
                .callCreatedEventToRestaurantApprovalRequestAvroModel(callCreatedEvent);


        when(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName()).thenReturn("test-topic");
        when(createCallCommandManager.createCallTransaction(any())).thenReturn(callCreatedEvent);
        // When
        EatsOrderResponseDto responseDto = eatsOrderCommandService.createAndPublishOrder(createEatsOrderCommandDto);

        // Then
        verify(kafkaProducer).send(topicNameCaptor.capture(), keyCaptor.capture(), messageCaptor.capture());

        assertEquals("test-topic", topicNameCaptor.getValue());
        assertEquals(requestAvroModel.getCallerId(), messageCaptor.getValue().getCallerId());
        assertEquals(requestAvroModel.getCalleeId(), messageCaptor.getValue().getCalleeId());
        assertEquals(requestAvroModel.getPrice(), messageCaptor.getValue().getPrice());
        assertEquals(requestAvroModel.getSagaId(), messageCaptor.getValue().getSagaId());
        assertEquals(requestAvroModel.getCreatedAt(), messageCaptor.getValue().getCreatedAt());

        assertEquals(requestAvroModel.getId(), callCreatedEvent.getCall().getId());
        assertEquals(requestAvroModel.getStatus(), Status.PENDING);
        assertEquals(callCreatedEvent.getCall().getId().getValue().toString(), keyCaptor.getValue());
    }
}