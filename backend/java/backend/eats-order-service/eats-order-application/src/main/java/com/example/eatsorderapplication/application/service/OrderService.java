package com.example.eatsorderapplication.application.service;

import com.example.commondata.dto.order.CreateOrderRequestDto;
import com.example.eatsorderapplication.application.component.AfterCommitEventPublisher;
import com.example.eatsorderapplication.mappers.Mapper;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.repository.OrderRepository;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.kafka.avro.model.RequestAvroModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.reactive.TransactionalEventPublisher;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Configuration
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final AfterCommitEventPublisher afterCommitEventPublisher;
    private final TransactionalOperator transactionalOperator;

    //    private final DataMapper dataMapper;
    private final RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;
    private final Sinks.Many<Message<RequestAvroModel>> sender;

    public OrderService(OrderRepository orderRepository,
                        AfterCommitEventPublisher afterCommitEventPublisher,
                        TransactionalOperator transactionalOperator,
                        RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository,
                        @Qualifier("restaurantApprovalSinks") Sinks.Many<Message<RequestAvroModel>> sender) {
        this.orderRepository = orderRepository;
        this.afterCommitEventPublisher = afterCommitEventPublisher;
        this.transactionalOperator = transactionalOperator;
        this.restaurantApprovalRequestOutboxRepository = restaurantApprovalRequestOutboxRepository;
        this.sender = sender;
    }

    /**
     * 메시지 변환
     * Dto <=> Domain <=> Avro Message or DB Entity
     * 서비스 레이어에서는 Domain Object 를 가지고 비즈니스 로직수행.
     *
     * @param createOrderRequestDto
     * @return
     */
    @Transactional
    public Mono<Void> createAndSaveOrder(CreateOrderRequestDto createOrderRequestDto) {
        OrderEntity orderEntity = Mapper.createOrderRequestDtoToOrderEntity(createOrderRequestDto);
        // 여기에는 domain object 가 존재하면 안됨. 제일 로직 코어에 존재해야함. dto 가 대신 피룡.
        Order order = Mapper.createOrderRequestDtoToOrder(createOrderRequestDto);
        return orderRepository.save(orderEntity)
            .doOnError(error -> log.error("DB error occurred during orderRepository.save: {}", error.getMessage(), error)) // DB 에러 로그
            .thenReturn(Mapper.orderToRestaurantApprovalOutboxEntity(order))
            .flatMap(restaurantApprovalRequestOutboxRepository::upsert)
            .doOnError(error -> log.error("DB error occurred during restaurantApprovalRequestOutboxRepository::upsert: {}", error.getMessage(), error)) // DB 에러 로그
            .then(afterCommitEventPublisher.publishEvent(Mapper.orderToRestaurantApprovalRequestAvro(order)))
            .then();
    }

    // 트랜잭션 커밋 이후에 받은 이벤트 처리.
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void dispatchAfterCommit(RequestAvroModel requestAvroModel) {
        var message = MessageBuilder.withPayload(requestAvroModel)
            .setHeader(IntegrationMessageHeaderAccessor.CORRELATION_ID, requestAvroModel.getOrderId())
            .setHeader(KafkaHeaders.PARTITION, 0) //TODO parition key by region
            .build();
        sender.emitNext(message, Sinks.EmitFailureHandler.FAIL_FAST);
    }

}
