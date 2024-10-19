package com.example.eatsorderapplication.application.messaging.processor;

import com.example.commondata.dto.order.AddressDto;
import com.example.commondata.dto.order.UserOrderAddressDto;
import com.example.eatsorderapplication.application.dto.DriverDetailsDto;
import com.example.eatsorderapplication.application.service.OrderService;
import com.example.eatsorderapplication.application.service.driver.Candidate;
import com.example.eatsorderapplication.application.service.driver.DriverService;
import com.example.eatsorderapplication.application.service.driver.Matching;
import com.example.eatsorderapplication.messaging.processor.DriverMatchingToNotificationProcessorImpl;
import com.example.kafka.avro.model.DriverMatchingRequestEvent;
import com.example.kafka.avro.model.UserNotificationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * spring boot 없이 MockitoExtension 으로 테스트 가능.
 */
@ExtendWith(MockitoExtension.class)
class DriverMatchingToNotificationProcessorImplTest {


    @Mock // @MockBean 으로 하면 안된다.
    private DriverService driverService;

    @Mock
    private OrderService orderService;

    @Mock
    private Sinks.Many<Tuple2<
            Set<Candidate>,
            Set<UserOrderAddressDto>>> getNearbyDriversSink;
    @Mock
    private Sinks.Many<Matching>
        matchedResultSink;
    private DriverMatchingToNotificationProcessorImpl processor;

    private Consumer<Flux<Message<DriverMatchingRequestEvent>>> driverMatchingRequestListener;
    private Supplier<Flux<Message<UserNotificationEvent>>> driverMatchedNotificationPublisher;
    private Supplier<Flux<Message<DriverMatchingRequestEvent>>> failedDriverMatchingResultPublisher;

    private int maxWindowCount;
    private Duration driverWindowMaxInterval;

    void init(int maxWindowCount, Duration driverWindowMaxInterval) {
        processor = new DriverMatchingToNotificationProcessorImpl(
            maxWindowCount,
            driverWindowMaxInterval,
            driverService,
            orderService,
            getNearbyDriversSink,
            matchedResultSink);
        driverMatchingRequestListener = processor.driverMatchingRequestListener();
        driverMatchedNotificationPublisher = processor.driverMatchedNotificationPublisher();
        failedDriverMatchingResultPublisher = processor.failedDriverMatchingResultPublisher();

    }

    void setUp1() {
        // MockitoAnnotations.openMocks(this); // @ExtendWith(MockitoExtension.class) 를 사용하면 매번 초기화 직접 할 필요 없음.
        maxWindowCount = 5;
        driverWindowMaxInterval = Duration.ofMillis(10000);
        init(maxWindowCount, driverWindowMaxInterval);
    }

    void setUp2() {
        // MockitoAnnotations.openMocks(this); // @ExtendWith(MockitoExtension.class) 를 사용하면 매번 초기화 직접 할 필요 없음.
        maxWindowCount = 1;
        driverWindowMaxInterval = Duration.ofMillis(10000);
        init(maxWindowCount, driverWindowMaxInterval);
    }

    void setUp3() {
        // MockitoAnnotations.openMocks(this); // @ExtendWith(MockitoExtension.class) 를 사용하면 매번 초기화 직접 할 필요 없음.
        maxWindowCount = 5;
        driverWindowMaxInterval = Duration.ofMillis(100000);
        init(maxWindowCount, driverWindowMaxInterval);
    }

    void setUp4() {
        // MockitoAnnotations.openMocks(this); // @ExtendWith(MockitoExtension.class) 를 사용하면 매번 초기화 직접 할 필요 없음.
        maxWindowCount = 5;
        driverWindowMaxInterval = Duration.ofMillis(10);
        init(maxWindowCount, driverWindowMaxInterval);
    }

    @Test
    void testWindowCount_thenGetNearbyDriversFromUsersIsNotCalled() {
        setUp1();
        // window 함수 테스트 할 때 주의점: source flux 가 완료되면 window 조건 충족 못해도 다음단계로 넘어감.
        // Given
        var correlationId = UUID.randomUUID().toString();
        DriverMatchingRequestEvent event = DriverMatchingRequestEvent.newBuilder()
            .setCorrelationId(correlationId)
            .setUserId("userId")
            .setCreatedAt(Instant.now())
            .build();

        Message<DriverMatchingRequestEvent> message = MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, correlationId)
            .build();

        var userOrderAddressDto = UserOrderAddressDto.builder()
            .orderId(correlationId)
            .userId("userId")
            .address(AddressDto.builder().build())
            .build();

        when(orderService.findUserAddressDtoByOrderId(UUID.fromString(correlationId))).thenReturn(Mono.just(userOrderAddressDto));

        // When
        Flux<Message<DriverMatchingRequestEvent>> infiniteFlux = Flux.concat(Flux.just(message), Flux.never());
        driverMatchingRequestListener.accept(infiniteFlux);
        // driverMatchingRequestListener.accept(Flux.just(message));

        // Then
        verify(driverService, never()).getNearbyDriversFromUsers(any());
    }

    @Test
    void testWindowCount_thenGetNearbyDriversFromUsersIsCalled() {
        setUp2();
        // Given
        var correlationId = UUID.randomUUID().toString();
        DriverMatchingRequestEvent event = DriverMatchingRequestEvent.newBuilder()
            .setCorrelationId(correlationId)
            .setUserId("userId")
            .setCreatedAt(Instant.now())
            .build();

        Message<DriverMatchingRequestEvent> message = MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, correlationId)
            .build();

        var userOrderAddressDto = UserOrderAddressDto.builder()
            .orderId(correlationId)
            .userId("userId")
            .address(AddressDto.builder().build())
            .build();

        when(orderService.findUserAddressDtoByOrderId(UUID.fromString(correlationId))).thenReturn(Mono.just(userOrderAddressDto));

        // When
        Flux<Message<DriverMatchingRequestEvent>> infiniteFlux = Flux.concat(Flux.just(message), Flux.never());
        driverMatchingRequestListener.accept(infiniteFlux);

        // Then
        verify(driverService, timeout(1000)).getNearbyDriversFromUsers(any());

    }

    @Test
    void testWindowInterval_thenGetNearbyDriversFromUsersIsNotCalled() {
        setUp3();
        // window 함수 테스트 할 때 주의점: source flux 가 완료되면 window 조건 충족 못해도 다음단계로 넘어감.
        // Given
        var correlationId = UUID.randomUUID().toString();
        DriverMatchingRequestEvent event = DriverMatchingRequestEvent.newBuilder()
            .setCorrelationId(correlationId)
            .setUserId("userId")
            .setCreatedAt(Instant.now())
            .build();

        Message<DriverMatchingRequestEvent> message = MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, correlationId)
            .build();

        var userOrderAddressDto = UserOrderAddressDto.builder()
            .orderId(correlationId)
            .userId("userId")
            .address(AddressDto.builder().build())
            .build();

        when(orderService.findUserAddressDtoByOrderId(UUID.fromString(correlationId))).thenReturn(Mono.just(userOrderAddressDto));

        // When
        Flux<Message<DriverMatchingRequestEvent>> infiniteFlux = Flux.concat(Flux.just(message), Flux.never());
        driverMatchingRequestListener.accept(infiniteFlux);
        // driverMatchingRequestListener.accept(Flux.just(message));

        // Then
        verify(driverService, never()).getNearbyDriversFromUsers(any());
    }

    @Test
    void testWindowInterval_thenGetNearbyDriversFromUsersIsCalled() {
        setUp4();
        // window 함수 테스트 할 때 주의점: source flux 가 완료되면 window 조건 충족 못해도 다음단계로 넘어감.
        // Given
        var correlationId = UUID.randomUUID().toString();
        DriverMatchingRequestEvent event = DriverMatchingRequestEvent.newBuilder()
            .setCorrelationId(correlationId)
            .setUserId("userId")
            .setCreatedAt(Instant.now())
            .build();

        Message<DriverMatchingRequestEvent> message = MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, correlationId)
            .build();

        var userOrderAddressDto = UserOrderAddressDto.builder()
            .orderId(correlationId)
            .userId("userId")
            .address(AddressDto.builder().build())
            .build();

        when(orderService.findUserAddressDtoByOrderId(UUID.fromString(correlationId))).thenReturn(Mono.just(userOrderAddressDto));

        // When
        Flux<Message<DriverMatchingRequestEvent>> infiniteFlux = Flux.concat(Flux.just(message), Flux.never());
        driverMatchingRequestListener.accept(infiniteFlux);
        // driverMatchingRequestListener.accept(Flux.just(message));

        // Then
        verify(driverService, timeout(1000)).getNearbyDriversFromUsers(any());
    }

    @Test
    void test_thenTryEmitNextIsCalled() {
        setUp1();
        // Given
        var correlationId = UUID.randomUUID().toString();
        DriverMatchingRequestEvent event = DriverMatchingRequestEvent.newBuilder()
            .setCorrelationId(correlationId)
            .setUserId("userId")
            .setCreatedAt(Instant.now())
            .build();

        Message<DriverMatchingRequestEvent> message = MessageBuilder.withPayload(event)
            .setHeader(KafkaHeaders.KEY, correlationId)
            .build();

        var userOrderAddressDto = UserOrderAddressDto.builder()
            .orderId(correlationId)
            .userId("userId")
            .address(AddressDto.builder().build())
            .build();

        Set<Candidate> t1 = new HashSet<>();
        Set<UserOrderAddressDto> t2 = new HashSet<>();


        when(orderService.findUserAddressDtoByOrderId(UUID.fromString(correlationId)))
            .thenReturn(Mono.just(userOrderAddressDto));
        when(driverService.getNearbyDriversFromUsers(any()))
            .thenReturn(Mono.just(Tuples.of(t1, t2)));

        // When
        driverMatchingRequestListener.accept(Flux.just(message));

        // Then
        ArgumentCaptor<Tuple2<Set<Candidate>, Set<UserOrderAddressDto>>> captor =
            ArgumentCaptor.forClass(Tuple2.class);

        verify(getNearbyDriversSink, timeout(1000)).tryEmitNext(captor.capture());

        Tuple2<Set<Candidate>, Set<UserOrderAddressDto>> capturedTuple = captor.getValue();

        assertNotNull(capturedTuple, "Captured Tuple3 should not be null");
//        assertEquals(driverDetails, capturedTuple.getT1(), "DriverDetailsDto set should match");
//        assertEquals(Set.of(userOrderAddressDto), capturedTuple.getT2(), "UserOrderAddressDto set should match");
//        assertTrue(capturedTuple.getT3().isEmpty(), "Third set in Tuple3 should be empty");

    }

//    @Test
//    void testDriverMatchingResultPublisher() {
//        // Given
//        UUID correlationId = UUID.randomUUID();
//        Matching matching = new Matching();
//        matching.setUserOrderAddress(new UserOrderAddressDto());
//        matching.getUserOrderAddress().setOrderId(correlationId.toString());
//        matching.getUserOrderAddress().setUserId("user123");
//
//        List<Matching> matchingList = Collections.singletonList(matching);
//
//        when(driverService.performMatching(any())).thenReturn(Flux.fromIterable(matchingList));
//
//        // When
//        Flux<Message<UserNotificationEvent>> publisherFlux = driverMatchingResultPublisher.get();
//
//        // Then
//        StepVerifier.create(publisherFlux)
//                .expectNextMatches(message -> {
//                    UserNotificationEvent payload = message.getPayload();
//                    return payload.getCorrelationId().equals(correlationId.toString()) &&
//                            payload.getUserId().equals("user123") &&
//                            payload.getMessage().isEmpty();
//                })
//                .verifyComplete();
//    }
//
//    @Test
//    void testFailedDriverMatchingResultPublisher() {
//        // Given
//        UUID correlationId = UUID.randomUUID();
//        UserOrderAddressDto failedUserOrderAddressDto = new UserOrderAddressDto();
//        failedUserOrderAddressDto.setOrderId(correlationId.toString());
//
//        // Simulate failed matching by adding to the third set in Tuple3
//        // Since getNearByDriversSink is private, we'll use reflection or modify the processor to expose it for testing
//        // For simplicity, assume we have access to emit directly
//
//        // Alternatively, mock the sink behavior
//        // Here, we'll skip detailed implementation due to complexity
//
//        // When
//        Flux<Message<DriverMatchingRequestEvent>> failedFlux = failedDriverMatchingResultPublisher.get();
//
//        // Then
//        // Since no failed events are emitted, expect no messages
//        StepVerifier.create(failedFlux)
//                .expectComplete()
//                .verify();
//    }
//
//    @Test
//    void testEndToEndFlow() {
//        // This test simulates the end-to-end flow from receiving a DriverMatchingRequestEvent to publishing a UserNotificationEvent
//
//        // Given
//        UUID correlationId = UUID.randomUUID();
//        DriverMatchingRequestEvent matchingEvent = DriverMatchingRequestEvent.newBuilder()
//                .setCorrelationId(correlationId.toString())
//                .build();
//
//        Message<DriverMatchingRequestEvent> message = MessageBuilder.withPayload(matchingEvent)
//                .setHeader(KafkaHeaders.KEY, correlationId.toString())
//                .build();
//
//        UserOrderAddressDto userOrderAddressDto = new UserOrderAddressDto();
//        userOrderAddressDto.setOrderId(correlationId.toString());
//
//        when(orderService.findUserAddressDtoByOrderId(correlationId)).thenReturn(Flux.just(userOrderAddressDto));
//
//        Set<DriverDetailsDto> driverDetailsDtos = new HashSet<>();
//        driverDetailsDtos.add(new DriverDetailsDto());
//
//        when(driverService.getNearbyDriversFromUsers(any())).thenReturn(Flux.just(driverDetailsDtos, new HashSet<>(), new HashSet<>()));
//
//        Matching matching = new Matching();
//        matching.setUserOrderAddress(userOrderAddressDto);
//        matching.setDriverDetails(new DriverDetailsDto());
//
//        when(driverService.performMatching(any())).thenReturn(Flux.just(matching));
//
//        // When
//        driverMatchingRequestListener.accept(Flux.just(message));
//
//        Flux<Message<UserNotificationEvent>> publisherFlux = driverMatchingResultPublisher.get();
//
//        // Then
//        StepVerifier.create(publisherFlux)
//                .expectNextMatches(msg ->
//                    msg.getPayload().getCorrelationId().equals(correlationId.toString()) &&
//                    msg.getPayload().getUserId().equals(userOrderAddressDto.getUserId())
//                )
//                .verifyComplete();
//
//        // Verify that acknowledgment was called
//        verify(orderService, timeout(1000)).findUserAddressDtoByOrderId(correlationId);
//        verify(driverService, timeout(1000)).getNearbyDriversFromUsers(any());
//        verify(driverService, timeout(1000)).performMatching(any());
//    }
}
