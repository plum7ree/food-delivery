package com.example.eatsorderdomain.data.mapper;

import com.example.commondata.domain.aggregate.Payment;
import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.domainentity.OrderItem;
import com.example.eatsorderdomain.data.dto.CreateOrderCommandDto;
import com.example.eatsorderdomain.data.dto.ResponseDto;
import com.example.eatsorderdomain.data.event.CallPaidEvent;
import com.example.kafka.avro.model.DriverApprovalRequestAvroModel;
import com.example.kafka.avro.model.DriverApprovalStatus;
import com.example.kafka.avro.model.RequestAvroModel;
import com.example.kafka.avro.model.ResponseAvroModel;
import org.apache.avro.Conversions;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;


public class DtoDataMapper {
    public static final Conversions.DecimalConversion decimalConversion = new Conversions.DecimalConversion();

    public static Order orderDtoToOrder(CreateOrderCommandDto createOrderCommandDto) {
        var orderId = new OrderId(UUID.randomUUID());
        return Order.builder()
            .id(orderId)
            .callerId(new CallerId(createOrderCommandDto.getCallerId()))
            .calleeId(new CalleeId(createOrderCommandDto.getCalleeId()))
            .price(new Money(createOrderCommandDto.getPrice()))
            .trackingId(new SimpleId(UUID.randomUUID()))
            .orderStatus(OrderStatus.PENDING)
            .address(createOrderCommandDto.getAddress())
            .items(createOrderCommandDto.getItems().stream().map(
                orderItemDto -> OrderItem.builder()
                    .id(new SimpleId(orderItemDto.getId()))
                    .orderId(orderId)
                    .productId(new SimpleId(orderItemDto.getId()))
                    .quantity(orderItemDto.getQuantity())
                    .price(new Money(orderItemDto.getPrice())) // this responsibility not belongs to here
                    .build()
            ).collect(Collectors.toList()))
            .build();
    }

    public static RequestAvroModel orderToRequestAvro(Order orderDomainObject, UUID sagaId) {
        return RequestAvroModel.newBuilder()
            .setId(UUID.randomUUID())
            .setPaymentId(UUID.randomUUID())
            .setOrderId(orderDomainObject.getId().getValue())
            .setSagaId(sagaId)
            .setCallerId(orderDomainObject.getCallerId().getValue())
            .setCalleeId(orderDomainObject.getCalleeId().getValue())
            .setOrderStatus(com.example.kafka.avro.model.OrderStatus.valueOf(orderDomainObject.getOrderStatus().name()))
            .setPrice(decimalConversion.toBytes(orderDomainObject.getPrice().getAmount(),
                RequestAvroModel.getClassSchema().getField("price").schema(),
                RequestAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
            .setTrackingId(orderDomainObject.getTrackingId().getValue())
            .setAddress(com.example.kafka.avro.model.Address.newBuilder()
                .setPostalCode(orderDomainObject.getAddress().getPostalCode())
                .setCity(orderDomainObject.getAddress().getCity())
                .setStreet(orderDomainObject.getAddress().getStreet())
                .build())
            .setItems(orderDomainObject.getItems().stream()
                .map(item -> com.example.kafka.avro.model.OrderItem.newBuilder()
                    .setId(item.getId().getValue())
                    .setQuantity(item.getQuantity())
                    .setPrice(decimalConversion.toBytes(
                        item.getPrice().getAmount(),
                        com.example.kafka.avro.model.OrderItem.getClassSchema().getField("price").schema(),
                        com.example.kafka.avro.model.OrderItem.getClassSchema().getField("price").schema().getLogicalType()))
                    .build())
                .collect(Collectors.toList()))
            .setCreatedAt(Instant.now())
            .build();
    }

    public static Order requestAvroToOrder(RequestAvroModel requestAvroModel) {
        return Order.builder()
            .id(new OrderId(UUID.fromString(requestAvroModel.getId().toString())))
            .callerId(new CallerId(UUID.fromString(requestAvroModel.getCallerId().toString())))
            .calleeId(new CalleeId(UUID.fromString(requestAvroModel.getCalleeId().toString())))
            .price(new Money(decimalConversion.fromBytes(
                requestAvroModel.getPrice(),
                requestAvroModel.getSchema().getField("price").schema(),
                requestAvroModel.getSchema().getField("price").schema().getLogicalType())))
            .trackingId(new SimpleId(UUID.fromString(requestAvroModel.getTrackingId().toString())))
            .orderStatus(OrderStatus.valueOf(requestAvroModel.getOrderStatus().name()))
            .address(Address.builder()
                .city(requestAvroModel.getAddress().getCity().toString())
                .postalCode(requestAvroModel.getAddress().getPostalCode().toString())
                .street(requestAvroModel.getAddress().getStreet().toString())
                .build())
            .items(requestAvroModel.getItems().stream()
                .map(item -> OrderItem.builder()
                    .id(new SimpleId(UUID.fromString(item.getId().toString())))
                    .quantity(item.getQuantity())
                    .price(new Money(decimalConversion.fromBytes(
                        item.getPrice(),
                        item.getSchema().getField("price").schema(),
                        item.getSchema().getField("price").schema().getLogicalType())))
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


    public static DriverApprovalRequestAvroModel callPaidEventToRequestAvroModel(CallPaidEvent domainEvent) {
        var call = domainEvent.getOrderDomainObject();
        return DriverApprovalRequestAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setDriverId(call.getCalleeId().getValue().toString())
            .setSagaId("")
            .setCallId(call.getId().getValue().toString())
            .setPrice(decimalConversion.toBytes(call.getPrice().getAmount(),
                DriverApprovalRequestAvroModel.getClassSchema().getField("price").schema(),
                DriverApprovalRequestAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
            .setDriverApprovalStatus(DriverApprovalStatus.PENDING)
            .setCreatedAt(domainEvent.getCreatedAt().toInstant())
            .build();
    }

    public static ResponseDto paymentResponseAvroToPaymentResponseDto(ResponseAvroModel model) {
        return ResponseDto.builder()
            .id(model.getId().toString())
            .sagaId(model.getSagaId().toString())
            .callId(model.getCallId().toString())
            .paymentId(model.getPaymentId().toString())
            .status(OrderStatus.valueOf(
                model.getOrderStatus().name()))
            .price(decimalConversion.fromBytes(model.getPrice(),
                model.getSchema().getField("price").schema(),
                model.getSchema().getField("price").schema().getLogicalType()))
            .createdAt(model.getCreatedAt())
            .failureMessages(model.getFailureMessages().toString())
            .build();
    }

    public static ResponseDto restaurantApprovalResponseToDto(ResponseAvroModel model) {
        return ResponseDto.builder()
            .id(model.getId().toString())
            .sagaId(model.getSagaId().toString())
            .driverId(model.getCalleeId().toString())
            .callId(model.getCallId().toString())
            .status(OrderStatus.valueOf(
                model.getOrderStatus().name()))
            .createdAt(model.getCreatedAt())
            .failureMessages(model.getFailureMessages().toString())
            .build();
    }

    public static Payment RequestAvroToPayment(RequestAvroModel model) {
        return Payment.builder()
            .id(new PaymentId(UUID.fromString(model.getPaymentId().toString())))
            .callerId(new CallerId(UUID.fromString(model.getCallerId().toString())))
            .calleeId(new CalleeId(UUID.fromString(model.getCalleeId().toString())))
            .orderId(new OrderId(UUID.fromString(model.getOrderId().toString())))
            .price(new Money(decimalConversion.fromBytes(model.getPrice(),
                model.getSchema().getField("price").schema(),
                model.getSchema().getField("price").schema().getLogicalType())))
            .status(OrderStatus.valueOf(model.getOrderStatus().name()))
            .build();

    }


}
