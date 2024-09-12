package com.example.eatsorderapplication.mappers;

import com.example.commondata.domain.aggregate.valueobject.Address;
import com.example.commondata.domain.events.notification.NotificationEvent;
import com.example.commondata.domain.events.order.OrderStatus;
import com.example.commondata.domain.events.order.OutboxStatus;
import com.example.commondata.dto.order.CreateOrderRequestDto;
import com.example.eatsorderdataaccess.entity.OrderAddressEntity;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.entity.OrderItemEntity;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.domainentity.OrderItem;
import com.example.kafka.avro.model.RequestAvroModel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.avro.Conversions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
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
                .build())
            .items(createOrderCommand.getItems().stream()
                .map(item -> OrderItem.builder()
                    .id(item.getId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build())
                .collect(Collectors.toList()))
            .orderStatus(OrderStatus.CREATED)
            .failureMessages(new ArrayList<>())
            .build();
    }

    public static OrderEntity createOrderRequestDtoToOrderEntity(CreateOrderRequestDto createOrderCommand) {
        OrderEntity orderEntity = OrderEntity.builder()
            .id(createOrderCommand.getOrderId())
            .customerId(createOrderCommand.getCallerId())
            .restaurantId(createOrderCommand.getCalleeId())
            .price(createOrderCommand.getPrice())
            .orderStatus(OrderStatus.CREATED.name())
            .failureMessages("")
            .build();

        var address = createOrderCommand.getAddress();
        OrderAddressEntity orderAddressEntity = OrderAddressEntity.builder()
            .id(UUID.randomUUID())
            .order(orderEntity)
            .street(address.getStreet())
            .postalCode(address.getPostalCode())
            .city(address.getCity())
            .build();

        var items = createOrderCommand.getItems();
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

    public static RestaurantApprovalOutboxMessageEntity orderToRestaurantApprovalOutboxEntity(Order order) {
        return RestaurantApprovalOutboxMessageEntity.builder()
            .id(UUID.randomUUID())
            .correlationId(order.getId())
            .status(OutboxStatus.CREATED.name())
            .build();
    }


    public static RequestAvroModel orderToRestaurantApprovalRequestAvro(Order orderDomainObject) {
        return RequestAvroModel.newBuilder()
            .setId(UUID.randomUUID())
            .setEventType("")
            .setPaymentId(UUID.randomUUID())
            .setOrderId(orderDomainObject.getId())
            .setCallerId(orderDomainObject.getCallerId())
            .setCalleeId(orderDomainObject.getCalleeId())
            .setOrderStatus(orderDomainObject.getOrderStatus().name())
            .setPrice(orderDomainObject.getPrice())
            .setAddress(com.example.kafka.avro.model.Address.newBuilder()
                .setPostalCode(orderDomainObject.getAddress().getPostalCode())
                .setCity(orderDomainObject.getAddress().getCity())
                .setStreet(orderDomainObject.getAddress().getStreet())
                .build())
            .setItems(orderDomainObject.getItems().stream()
                .map(item -> com.example.kafka.avro.model.OrderItem.newBuilder()
                    .setId(item.getId())
                    .setQuantity(item.getQuantity())
                    .setPrice(item.getPrice())
                    .build())
                .collect(Collectors.toList()))
            .setCreatedAt(Instant.now())
            .build();
    }

    public static Order requestAvroToOrder(RequestAvroModel requestAvroModel) {
        return Order.builder()
            .id(requestAvroModel.getId())
            .callerId(requestAvroModel.getCallerId())
            .calleeId(requestAvroModel.getCalleeId())
            .price(requestAvroModel.getPrice())
            .orderStatus(OrderStatus.valueOf(requestAvroModel.getOrderStatus().toString()))
            .address(Address.builder()
                .city(requestAvroModel.getAddress().getCity().toString())
                .postalCode(requestAvroModel.getAddress().getPostalCode().toString())
                .street(requestAvroModel.getAddress().getStreet().toString())
                .build())
            .items(requestAvroModel.getItems().stream()
                .map(item -> OrderItem.builder()
                    .id(item.getId())
                    .quantity(item.getQuantity())
                    .price(item.getPrice())
                    .build())
                .collect(Collectors.toList()))
            .failureMessages(requestAvroModel.getFailureMessages() != null
                ? new ArrayList<>(requestAvroModel.getFailureMessages()
                .stream()
                .map(CharSequence::toString)
                .collect(Collectors.toList()))
                : null)
            .build();
    }

    public static NotificationEvent toNotificationCreated(Order order) {
        // UUID 및 다른 필드를 변환하여 NotificationCreated 객체 생성
        return NotificationEvent.NotificationCreated.builder()
            .orderId(order.getId())
            .userId(order.getCallerId())  // Assuming CallerId is the user ID
            .notificationType(NotificationEvent.NotificationType.ORDER_APPROVED)  // You may need to adjust this
            .message("Your order has been approved!")  // Example message
            .orderDetails(NotificationEvent.OrderDetails.builder()
                .orderId(order.getId())
                .totalAmount(order.getPrice().intValue())  // Convert Double to Integer
                .build())
            .driverDetails(NotificationEvent.DriverDetails.builder()
                .driverId("driver-id-placeholder")  // Example placeholder, adjust according to your logic
                .lat(0.0)  // Example placeholder
                .lon(0.0)  // Example placeholder
                .build())
            .createdAt(Instant.now())  // Current timestamp
            .build();
    }


}
