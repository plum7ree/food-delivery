package com.example.eatsorderdomain.data.mapper;

import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.eatsorderdomain.data.aggregate.OrderDomainObject;
import com.example.eatsorderdomain.data.dto.CreateOrderCommandDto;
import com.example.eatsorderdomain.data.dto.ResponseDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.eatsorderdomain.data.event.CallPaidEvent;
import com.example.kafka.avro.model.*;
import org.apache.avro.Conversions;

import java.util.UUID;


public class DataMapper {
    public static final Conversions.DecimalConversion decimalConversion = new Conversions.DecimalConversion();

    public static OrderDomainObject orderDtoToOrderDO(CreateOrderCommandDto createOrderCommandDto) {
        return OrderDomainObject.builder()
            .id(new OrderId(UUID.randomUUID()))
            .callerId(new CallerId(createOrderCommandDto.getCallerId()))
            .calleeId(new CalleeId(createOrderCommandDto.getCalleeId()))
            .price(new Money(createOrderCommandDto.getPrice()))
            .trackingId(new SimpleId(UUID.randomUUID()))
            .orderStatus(OrderStatus.PENDING)
            .address(createOrderCommandDto.getAddress())
            .items(createOrderCommandDto.getItems())
            .build();
    }

    public static RequestAvroModel callCreatedEventToRestaurantApprovalRequestAvroModel(CallCreatedEvent domainEvent) {
        var call = domainEvent.getOrderDomainObject();
        return RequestAvroModel.newBuilder()
            .setId(UUID.randomUUID().toString())
            .setCallId(call.getId().getValue().toString())
            .setSagaId("") // saga not yet involved.
            .setCallerId(call.getCallerId().getValue().toString())
            .setCalleeId(call.getCalleeId().getValue().toString())
            .setPrice(decimalConversion.toBytes(call.getPrice().getAmount(),
                PaymentRequestAvroModel.getClassSchema().getField("price").schema(),
                PaymentRequestAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
            //TODO this is unsafe, might case runtime error. please use Type Token to check this at compile time.
            .setStatus(Status.valueOf(domainEvent.getOrderDomainObject().getOrderStatus().toString()))
            .setCreatedAt(domainEvent.getCreatedAt().toInstant())
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
            .status(Status.valueOf(
                model.getStatus().name()))
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
            .status(Status.valueOf(
                model.getStatus().name()))
            .createdAt(model.getCreatedAt())
            .failureMessages(model.getFailureMessages().toString())
            .build();
    }
}
