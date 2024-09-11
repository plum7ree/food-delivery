package com.example.restaurantapprovalservice.service.listener.kafka;

import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderdataaccess.mapper.EntityDtoMapper;
import com.example.eatsorderdataaccess.repository.OrderApprovalRepository;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.mapper.DtoDataMapper;
import com.example.kafka.avro.model.RequestAvroModel;
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
import java.util.UUID;

import static com.example.commondata.domain.aggregate.valueobject.SagaType.EATS_ORDER;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestaurantApprovalRequestKafkaConsumer {

    private KafkaConsumer<String, RequestAvroModel> consumer;


    @Value("${kafka-consumer-group-id.restaurant-approval-request-consumer-group-id}")
    private String groupId;

    @Value("${topic-names.restaurant-approval-request-topic-name}")
    private String topicName;

    private final OrderApprovalRepository orderApprovalRepository;
    private final RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;


    @Resource(name = "commonKafkaConsumerConfigs")
    // bean 이 map 인 경우는 @Resource 를 사용해야한다. 안그러면 map of map 리 리턴되어서 이름으로 다시 찾아야함.
    private Map<String, Object> commonConsumerConfigs;


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
        log.info("processRecord: {}", message);

        try {
            OrderStatus orderStatus = OrderStatus.valueOf(message.getOrderStatus().name());
            if (OrderStatus.PENDING == orderStatus) {
                log.info("Processing payment for order id: {}", message.getPaymentId());
                complete(DtoDataMapper
                    .requestAvroToOrder(message), message.getSagaId());
            } else if (OrderStatus.USER_CANCELED == orderStatus) {
//                log.info("Cancelling payment for order id: {}", paymentRequestAvroModel.getOrderId());
//                paymentRequestMessageListener.cancel(paymentMessagingDataMapper
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
    public void complete(Order order, UUID sagaId) throws Exception {
        // 1. check Status.APPROVED already in restaurant approval request database
        try {
            if (orderApprovalRepository
                .findByOrderIdAndStatus(order.getId().getValue(), RestaurantApprovalStatus.APPROVED.name())
                .isPresent()) {
                return;
            }
        } catch (Exception e) {
            throw new Exception("error orderApprovalRepository.findByOrderIdAndStatus() : " + e.getMessage());
        }

        // validate(order);
        try {
            var entity = EntityDtoMapper.orderToOrderApproval(order, RestaurantApprovalStatus.APPROVED);
            orderApprovalRepository.save(entity);
        } catch (Exception e) {
            throw new Exception("error orderApprovalRepository.save(entity): " + e.getMessage());
        }
        try {
            order.setOrderStatus(OrderStatus.RESTAURANT_APPROVED);
            var entity = EntityDtoMapper.orderToRestaurantApprovalOutboxMessageEntity(
                order,
                sagaId,
                EATS_ORDER.name(),
                OutboxStatus.STARTED,
                SagaStatus.NOT_USED
            );

            restaurantApprovalRequestOutboxRepository.upsert(entity);
        } catch (Exception e) {
            throw new Exception("error restaurantApprovalRequestOutboxRepository.save(entity): " + e.getMessage());
        }

    }


}

