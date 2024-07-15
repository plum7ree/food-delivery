package com.example.eatsorderapplication.service;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalRequestEntity;
import com.example.eatsorderdataaccess.repository.jpa.OrderRepository;
import com.example.eatsorderdataaccess.repository.jpa.RestaurantApprovalRequestOutboxRepository;
import com.example.eatsorderdomain.data.aggregate.OrderDomainObject;
import com.example.eatsorderdomain.data.dto.CreateOrderCommandDto;
import com.example.eatsorderdomain.data.mapper.DataMapper;
import com.example.kafka.avro.model.RequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EatsOrderCommandService {

    //    private final DataMapper dataMapper;
    private final EatsOrderServiceConfigData eatsOrderServiceConfigData;
    private final KafkaProducer<String, RequestAvroModel> kafkaProducer;
    private final ObjectMapper objectMapper;
    private final OrderRepository orderRepository;
    private final RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;
    @Transactional
    public EatsOrderResponseDto createAndPublishOrder(CreateOrderCommandDto createOrderCommandDto) {
        try {
            // 1. create transaction object
            OrderDomainObject orderDomainObject = DataMapper.orderDtoToOrderDO(createOrderCommandDto);
            // check call element (price > 0, callstatus, ) valid.
            orderDomainObject.validateOrder();

            // 2. save it into call db
            log.info("call saved. Id: {}", orderDomainObject.getId().getValue());
            {
                UUID id = orderDomainObject.getId().getValue();
                UUID customerId = orderDomainObject.getCallerId().getValue();
                UUID restaurantId = orderDomainObject.getCalleeId().getValue();
                var trackingId = orderDomainObject.getTrackingId().getValue();
                var price = orderDomainObject.getPrice().getAmount();
                var orderStatus = orderDomainObject.getOrderStatus();
                var failureMessages = "";
                var entity = OrderEntity.builder()
                    .id(id)
                    .customerId(customerId)
                    .restaurantId(restaurantId)
                    .trackingId(trackingId)
                    .price(price)
                    .orderStatus(orderStatus)
                    .failureMessages(failureMessages)
                    .build();

                orderRepository.save(entity);
            }

            {
                // 3. save event into order outbox table for debezium to trigger
                UUID id = UUID.randomUUID();
                UUID sagaId = UUID.randomUUID();
                ZonedDateTime createdAt = ZonedDateTime.now(ZoneId.of("UTC"));
                ZonedDateTime processedAt = ZonedDateTime.now(ZoneId.of("UTC"));
                String type = "APPROVAL";
                String payload = "{}";  // JSON payload example
                OrderStatus orderStatus = OrderStatus.PENDING;  // Make sure this matches the enum value
                OutboxStatus outboxStatus = OutboxStatus.STARTED;  // Make sure this matches the enum value
                SagaStatus sagaStatus = SagaStatus.STARTED;
                int version = 1;
                var entity = RestaurantApprovalRequestEntity.builder()
                    .id(id)
                    .sagaId(sagaId)
                    .createdAt(createdAt)
                    .processedAt(processedAt)
                    .type(type)
                    .payload(payload)
                    .orderStatus(orderStatus)
                    .outboxStatus(outboxStatus)
                    .sagaStatus(sagaStatus)
                    .version(version)
                    .build();

                restaurantApprovalRequestOutboxRepository.save(entity);
            }


            return EatsOrderResponseDto.builder().orderStatus(orderDomainObject.getOrderStatus()).callTrackingId(orderDomainObject.getTrackingId().getValue()).build();

        } catch (Exception e) {
            log.error(e.toString());
            //TODO save error into error db.
        }
        return null;
    }


}
