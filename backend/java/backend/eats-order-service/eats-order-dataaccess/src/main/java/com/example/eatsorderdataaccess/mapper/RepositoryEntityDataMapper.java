package com.example.eatsorderdataaccess.mapper;


import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderdataaccess.entity.*;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.example.commondata.domain.aggregate.valueobject.SagaType.EATS_ORDER;


public class RepositoryEntityDataMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static OrderEntity orderToOrderEntity(Order order) {
        OrderEntity orderEntity = OrderEntity.builder()
            .id(order.getId().getValue())
            .customerId(order.getCallerId().getValue())
            .restaurantId(order.getCalleeId().getValue())
            .trackingId(order.getTrackingId().getValue())
            .price(order.getPrice().getAmount())
            .orderStatus(order.getOrderStatus())
            .failureMessages(order.getFailureMessages() != null ? String.join(", ", order.getFailureMessages()) : "")
            .build();

        OrderAddressEntity orderAddressEntity = OrderAddressEntity.builder()
            .id(UUID.randomUUID())
            .order(orderEntity)
            .street(order.getAddress().getStreet())
            .postalCode(order.getAddress().getPostalCode())
            .city(order.getAddress().getCity())
            .build();

        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        for (int i = 0; i < order.getItems().size(); i++) {
            var item = order.getItems().get(i);
            var orderItemEntity = OrderItemEntity.builder()
                .id((long) i + 1)
                .order(orderEntity)
                .productId(item.getProductId().getValue())
                .price(item.getPrice().getAmount())
                .quantity(item.getQuantity())
                .subTotal(item.getPrice().getAmount().multiply(BigDecimal.valueOf(item.getQuantity())))
                .build();
            orderItemEntities.add(orderItemEntity);
        }

        orderEntity.setAddress(orderAddressEntity);
        orderEntity.setItems(orderItemEntities);

        return orderEntity;
    }

    public static RestaurantApprovalOutboxMessageEntity orderToRestaurantApprovalOutboxMessageEntity(
        Order order,
        UUID sagaId,
        String sagaType,
        OutboxStatus outboxStatus,
        SagaStatus sagaStatus) throws JsonProcessingException {
        return RestaurantApprovalOutboxMessageEntity.builder()
            .id(UUID.randomUUID())
            .sagaId(sagaId)
            .createdAt(ZonedDateTime.now(ZoneId.of("UTC")))
            .processedAt(ZonedDateTime.now(ZoneId.of("UTC")))
            .sagaType(sagaType)
            .payload(objectMapper.writeValueAsString(order))
            .orderStatus(order.getOrderStatus())
            .outboxStatus(outboxStatus)
            .sagaStatus(sagaStatus)
            .version(1)
            .build();
    }

    public static OrderApprovalEntity orderToOrderApproval(Order order, RestaurantApprovalStatus restaurantApprovalStatus) {
        return OrderApprovalEntity.builder()
            .id(UUID.randomUUID())
            .orderId(order.getId().getValue())
            .status(restaurantApprovalStatus)
            .restaurantId(order.getCalleeId().getValue())
            .build();
    }


}