package com.example.eatsorderapplication.service;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderapplication.data.dto.EatsOrderResponseDto;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdataaccess.repository.jdbc.OrderAppDao;
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
    private final OrderAppDao orderDao;
    private final RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;
    private final ObjectMapper objectMapper;

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
                orderDao.insertOrder(id, customerId, restaurantId, trackingId, price, orderStatus, failureMessages);
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
                orderDao.insertRestaurantApproval(id, sagaId, createdAt, processedAt, type, payload, orderStatus, outboxStatus, sagaStatus, version);

            }


            return EatsOrderResponseDto.builder().orderStatus(orderDomainObject.getOrderStatus()).callTrackingId(orderDomainObject.getTrackingId().getValue()).build();

        } catch (Exception e) {
            log.error(e.toString());
            //TODO save error into error db.
        }
        return null;
    }


}
