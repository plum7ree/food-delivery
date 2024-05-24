package com.example.eatsorderdomain.data.mapper;

import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.eatsorderdomain.data.dto.CreateEatsOrderCommandDto;
import com.example.eatsorderdomain.data.dto.ResponseDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.eatsorderdomain.data.event.CallPaidEvent;
import com.example.commondata.domain.aggregate.valueobject.CallStatus;
import com.example.commondata.domain.aggregate.valueobject.DriverId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.UserId;
import com.example.kafka.avro.model.*;
import lombok.RequiredArgsConstructor;
import org.apache.avro.Conversions;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("callDomainDataMapper")
@RequiredArgsConstructor
public class DataMapper {
    private final Conversions.DecimalConversion decimalConversion = new Conversions.DecimalConversion();

    public Call createCallCommandDtoToCall(CreateEatsOrderCommandDto createEatsOrderCommandDto) {
        return Call.builder()
                .driverId(new DriverId(createEatsOrderCommandDto.getDriverId()))
                .userId(new UserId(createEatsOrderCommandDto.getUserId()))
                .price(new Money(createEatsOrderCommandDto.getPrice()))
                .callStatus(CallStatus.PENDING)
                .build();
    }

    public RequestAvroModel callCreatedEventToRestaurantApprovalRequestAvroModel(CallCreatedEvent domainEvent) {
        var call = domainEvent.getCall();
        return RequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setCallId(call.getId().getValue().toString())
                .setSagaId("") // saga not yet involved.
                .setCallerId(call.getUserId().getValue().toString())
                .setPrice(decimalConversion.toBytes(call.getPrice().getAmount(),
                        PaymentRequestAvroModel.getClassSchema().getField("price").schema(),
                        PaymentRequestAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
                .setStatus(Status.PENDING)
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .build();
    }

    public DriverApprovalRequestAvroModel callPaidEventToRequestAvroModel(CallPaidEvent domainEvent) {
        var call = domainEvent.getCall();
        return DriverApprovalRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setDriverId(call.getDriverId().getValue().toString())
                .setSagaId("")
                .setCallId(call.getId().getValue().toString())
                .setPrice(decimalConversion.toBytes(call.getPrice().getAmount(),
                        DriverApprovalRequestAvroModel.getClassSchema().getField("price").schema(),
                        DriverApprovalRequestAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
                .setDriverApprovalStatus(DriverApprovalStatus.PENDING)
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .build();
    }

    public ResponseDto paymentResponseAvroToPaymentResponseDto(ResponseAvroModel model) {
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

    public ResponseDto restaurantApprovalResponseToDto(ResponseAvroModel model) {
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
