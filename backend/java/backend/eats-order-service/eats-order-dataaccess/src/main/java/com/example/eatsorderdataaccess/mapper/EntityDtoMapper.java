package com.example.eatsorderdataaccess.mapper;


import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.commondata.domain.events.order.OrderStatus;
import com.example.commondata.domain.events.order.OutboxStatus;
import com.example.eatsorderdataaccess.entity.*;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.dto.CreateOrderRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class EntityDtoMapper {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static OrderEntity createOrderRequestToOrderEntity(CreateOrderRequest createOrderRequest) {
        OrderEntity orderEntity = OrderEntity.builder()
            .id(createOrderRequest.getOrderId())
            .customerId(createOrderRequest.getCallerId())
            .restaurantId(createOrderRequest.getCalleeId())
            .price(createOrderRequest.getPrice())
            .trackingId(UUID.randomUUID())
            .orderStatus(OrderStatus.PENDING.name())
            .failureMessages("")
            .build();

        var address = createOrderRequest.getAddress();
        OrderAddressEntity orderAddressEntity = OrderAddressEntity.builder()
            .id(UUID.randomUUID())
            .order(orderEntity)
            .street(address.getStreet())
            .postalCode(address.getPostalCode())
            .city(address.getCity())
            .build();

        var items = createOrderRequest.getItems();
        List<OrderItemEntity> orderItemEntities = new ArrayList<>();
        for (int i = 0; i < items.size(); i++) {
            var item = items.get(i);
            var orderItemEntity = OrderItemEntity.builder()
                .id((long) i + 1)
                .order(orderEntity)
                .productId(item.getId())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .subTotal(item.getPrice() * item.getQuantity())
                .build();
            orderItemEntities.add(orderItemEntity);
        }
        orderEntity.setAddress(orderAddressEntity);
        orderEntity.setItems(orderItemEntities);

        return orderEntity;
    }

    public static RestaurantApprovalOutboxMessageEntity orderEntityToRestaurantApprovalOutboxEntity(OrderEntity order) {
        return RestaurantApprovalOutboxMessageEntity.builder()
            .id(UUID.randomUUID())
            .correlationId(order.getId())
            .status(OutboxStatus.SUCCESS.name())
            .build();
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
            .orderStatus(order.getOrderStatus().name())
            .outboxStatus(outboxStatus.name())
            .sagaStatus(sagaStatus.name())
            .version(1)
            .build();
    }

    public static OrderApprovalEntity orderToOrderApproval(Order order, RestaurantApprovalStatus restaurantApprovalStatus) {
        return OrderApprovalEntity.builder()
            .id(UUID.randomUUID())
            .orderId(order.getId().getValue())
            .status(restaurantApprovalStatus.name())
            .restaurantId(order.getCalleeId().getValue())
            .build();
    }


}