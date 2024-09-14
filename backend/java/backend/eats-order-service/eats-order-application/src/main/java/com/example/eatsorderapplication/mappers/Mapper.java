package com.example.eatsorderapplication.mappers;

import com.example.commondata.domain.aggregate.valueobject.Address;
import com.example.commondata.domain.events.order.OrderStatus;
import com.example.commondata.domain.events.order.OutboxStatus;
import com.example.commondata.dto.order.CreateOrderRequestDto;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.domainentity.OrderItem;
import com.example.kafka.avro.model.RequestApproval;
import com.example.kafka.avro.model.RestaurantEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Conversions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;


public class Mapper {
    public static final Conversions.DecimalConversion decimalConversion = new Conversions.DecimalConversion();

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static Order createOrderRequestDtoToOrder(CreateOrderRequestDto createOrderCommand) {
        return Order.builder()
            .id(createOrderCommand.getOrderId())
            .callerId(createOrderCommand.getCallerId())
            .calleeId(createOrderCommand.getCalleeId())
            .price(createOrderCommand.getPrice())
            .address(Address.builder()
                .city(createOrderCommand.getAddress().getCity())
                .postalCode(createOrderCommand.getAddress().getPostalCode())
                .street(createOrderCommand.getAddress().getStreet())
                .lat(createOrderCommand.getAddress().getLat())
                .lon(createOrderCommand.getAddress().getLon())
                .build())
            .items(createOrderCommand.getItems().stream()
                .map(item -> OrderItem.builder()
                    .id(item.getId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .subTotal(item.getPrice() * item.getQuantity())
                    .build())
                .collect(Collectors.toList()))
            .orderStatus(OrderStatus.CREATED)
            .failureMessages(new ArrayList<>())
            .build();
    }


    public static RestaurantApprovalOutboxMessageEntity orderToRestaurantApprovalOutboxEntity(Order order) {
        return RestaurantApprovalOutboxMessageEntity.builder()
            .id(UUID.randomUUID())
            .correlationId(order.getId())
            .status(OutboxStatus.CREATED.name())
            .build();
    }


    public static RestaurantEvent orderToRequestRestaurantApprovalEvent(Order order) {
        return RestaurantEvent.newBuilder()
            .setCorrelationId(order.getId().toString())
            .setEvent(RequestApproval.newBuilder()
                .setOrderId(order.getId().toString())
                .setCreatedAt(Instant.now())
                .build())
            .build();
    }


}
