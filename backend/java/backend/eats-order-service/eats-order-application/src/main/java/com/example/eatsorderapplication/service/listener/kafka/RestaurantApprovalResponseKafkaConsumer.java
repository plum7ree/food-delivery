package com.example.eatsorderapplication.service.listener.kafka;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderapplication.service.saga.EatsOrderSaga;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import com.example.eatsorderdataaccess.mapper.RepositoryEntityDataMapper;
import com.example.eatsorderdataaccess.repository.OrderApprovalRepository;
import com.example.eatsorderdataaccess.repository.PaymentOutboxRepository;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.mapper.DtoDataMapper;
import com.example.kafka.avro.model.RequestAvroModel;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantApprovalResponseKafkaConsumer {

    private KafkaConsumer<String, RequestAvroModel> consumer;


    @Value("${kafka-consumer-group-id.restaurant-approval-response-consumer-group-id}")
    private String groupId;

    @Value("${topic-names.restaurant-approval-response-topic-name}")
    private String topicName;

    private final OrderApprovalRepository orderApprovalRepository;

    private final RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;
    @Resource(name = "commonKafkaConsumerConfigs")
    // bean 이 map 인 경우는 @Resource 를 사용해야한다. 안그러면 map of map 리 리턴되어서 이름으로 다시 찾아야함.
    private Map<String, Object> commonConsumerConfigs;

    private final EatsOrderSaga eatsOrderSaga;

    private final PaymentOutboxRepository paymentOutboxRepository;

    @PostConstruct
    public void init() {
        Map<String, Object> props = commonConsumerConfigs;
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));

        new Thread(this::pollMessages).start();
    }

    @PreDestroy
    public void cleanup() {
        if (consumer != null) {
            consumer.close();
        }
    }

    private void pollMessages() {
        try {
            while (true) {
                ConsumerRecords<String, RequestAvroModel> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(record -> {
                    if (processRecord(record.value())) {
                        consumer.commitSync(Collections.singletonMap(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                        ));
                    } else {
                        consumer.commitSync(Collections.singletonMap(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset())
                        ));
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error in Kafka polling loop: {}", e.getMessage());
        } finally {
            consumer.close();
        }
    }

    private boolean processRecord(RequestAvroModel message) {
        log.info("coupon issue request topic received: {}", message);

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(message.getOrderStatus().name());
            if (OrderStatus.CALLEE_APPROVED == orderStatus) {
                log.info("Processing payment for order id: {}", message.getPaymentId());
                complete(DtoDataMapper
                    .requestAvroToOrder(message), UUID.fromString(message.getSagaId().toString()));
            } else if (OrderStatus.CALLEE_REJECTED == orderStatus) {
//                log.info("Cancelling payment for order id: {}", paymentRequestAvroModel.getOrderId());
//                paymentRequestMessageListener.rollback(paymentMessagingDataMapper
//                    .paymentRequestAvroModelToPaymentRequest(paymentRequestAvroModel));
            }

            //
        } catch (Exception e) {
            log.error("Error processing message: {}, exception: {}", message, e.getMessage());
            return false;
        }
        return true;
    }

    @Transactional
    public void complete(Order order, UUID sagaId) throws JsonProcessingException {
        // 1. check Status.APPROVED already in restaurant approval request database
        if (orderApprovalRepository
            .findByOrderIdAndStatus(order.getId().getValue(), RestaurantApprovalStatus.APPROVED)
            .isPresent()) {
            return;
        }

        // validate(order);
        {
            var entity = RepositoryEntityDataMapper.orderToOrderApproval(order, RestaurantApprovalStatus.APPROVED);
            orderApprovalRepository.save(entity);
        }

        updateOutboxRepositories(order, sagaId);


    }


    @Transactional
    public void rollback(Order order, UUID sagaId) throws JsonProcessingException {
        // 1. check Status.APPROVED already in restaurant approval request database
        if (orderApprovalRepository
            .findByOrderIdAndStatus(order.getId().getValue(), RestaurantApprovalStatus.APPROVED)
            .isPresent()) {
            return;
        }

        // validate(order);
        {
            var entity = RepositoryEntityDataMapper.orderToOrderApproval(order, RestaurantApprovalStatus.APPROVED);
            orderApprovalRepository.save(entity);
        }

        updateOutboxRepositories(order, sagaId);


    }

    @Transactional
    public void updateOutboxRepositories(Order order, UUID sagaId) {
        Optional<RestaurantApprovalOutboxMessageEntity> optionalRestaurantApprovalOutboxMessageEntity =
            restaurantApprovalRequestOutboxRepository.findById(sagaId);

        optionalRestaurantApprovalOutboxMessageEntity.ifPresent(
            restaurantApprovalOutboxMessageEntity -> {
                SagaStatus currSagaStatus = optionalRestaurantApprovalOutboxMessageEntity.get().getSagaStatus();
                SagaStatus newSagaStatus = eatsOrderSaga.updateSagaStatus(currSagaStatus, order.getOrderStatus());

                restaurantApprovalOutboxMessageEntity.setSagaStatus(newSagaStatus);

                // update
                restaurantApprovalRequestOutboxRepository.save(optionalRestaurantApprovalOutboxMessageEntity.get());

                // payment outbox 에도 업데이트된 sagaStatus 업데이트해야한다. 안 그러면 PROCESSING (PAID 된 상태) 계속 쌓임.
                var optionalPaymentOutboxMessageEntity = paymentOutboxRepository.findBySagaIdAndSagaStatus(sagaId.toString(), currSagaStatus.name());

                optionalPaymentOutboxMessageEntity.ifPresent(
                    paymentOutboxMessageEntity -> {
                        paymentOutboxMessageEntity.setSagaStatus(newSagaStatus);
                        paymentOutboxRepository.save(paymentOutboxMessageEntity);
                    }
                );
            }
        );
    }


}

