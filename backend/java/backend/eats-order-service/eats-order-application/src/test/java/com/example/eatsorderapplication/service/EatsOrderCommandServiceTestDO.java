package com.example.eatsorderapplication.service;

import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import com.example.eatsorderdataaccess.repository.OrderRepository;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.dto.CreateOrderCommandDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.kafka.avro.model.RequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;

    @Captor
    private ArgumentCaptor<OrderEntity> orderEntityCaptor;

    @Captor
    private ArgumentCaptor<RestaurantApprovalOutboxMessageEntity> restaurantApprovalRequestEntityCaptor;

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


        Order callAfterCreateOrderTransactionDO = Order.builder()
            .id(callId)
            .calleeId(calleeId)
            .callerId(callerId)
            .price(money)
            .trackingId(new SimpleId(UUID.randomUUID()))
            .orderStatus(OrderStatus.PENDING)
            .orderStatus(OrderStatus.PENDING)
            .build();

        CallCreatedEvent callCreatedEvent = new CallCreatedEvent(callAfterCreateOrderTransactionDO, nowMock);

        MockedStatic<ZonedDateTime> zonedDateTimeMockedStatic = Mockito.mockStatic(ZonedDateTime.class);
        zonedDateTimeMockedStatic.when(() -> ZonedDateTime.now(any(ZoneId.class))).thenReturn(nowMock);
        when(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName()).thenReturn("test-topic");
        // When
        EatsOrderResponseDto responseDto = eatsOrderCommandService.createAndSaveOrder(createEatsOrderCommandDto);

        // Then

        // Verify OrderRepository save call
        verify(orderRepository).save(orderEntityCaptor.capture());
        OrderEntity savedOrderEntity = orderEntityCaptor.getValue();
        assertNotNull(savedOrderEntity);
        assertEquals(userId, savedOrderEntity.getCustomerId());
        assertEquals(driverId, savedOrderEntity.getRestaurantId());
        assertEquals(price, savedOrderEntity.getPrice());
        assertEquals(OrderStatus.PENDING, savedOrderEntity.getOrderStatus());

        // Verify RestaurantApprovalRequestOutboxRepository save call
        verify(restaurantApprovalRequestOutboxRepository).save(restaurantApprovalRequestEntityCaptor.capture());
        RestaurantApprovalOutboxMessageEntity savedRestaurantApprovalRequestEntity = restaurantApprovalRequestEntityCaptor.getValue();
        assertNotNull(savedRestaurantApprovalRequestEntity);
        assertEquals(OrderStatus.PENDING, savedRestaurantApprovalRequestEntity.getOrderStatus());
        assertEquals(OutboxStatus.STARTED, savedRestaurantApprovalRequestEntity.getOutboxStatus());
        assertEquals(SagaStatus.STARTED, savedRestaurantApprovalRequestEntity.getSagaStatus());
        assertEquals("APPROVAL", savedRestaurantApprovalRequestEntity.getSagaType());

    }
}