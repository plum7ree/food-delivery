package com.example.calldomain.data.mapper;

import com.example.calldomain.data.aggregate.Call;
import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.calldomain.data.dto.DriverApprovalResponseDto;
import com.example.calldomain.data.dto.PaymentResponseDto;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.calldomain.data.event.CallPaidEvent;
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

    public Call createCallCommandDtoToCall(CreateCallCommandDto createCallCommandDto) {
        return Call.builder()
                .driverId(new DriverId(createCallCommandDto.getDriverId()))
                .userId(new UserId(createCallCommandDto.getUserId()))
                .price(new Money(createCallCommandDto.getPrice()))
                .callStatus(CallStatus.PENDING)
                .build();
    }

    public PaymentRequestAvroModel callCreatedEventToPaymentRequestAvroModel(CallCreatedEvent domainEvent) {
        var call = domainEvent.getCall();
        return PaymentRequestAvroModel.newBuilder()
                .setId(UUID.randomUUID().toString())
                .setCallId(call.getId().getValue().toString())
                .setSagaId("") // saga not yet involved.
                .setUserId(call.getUserId().getValue().toString())
                .setPrice(decimalConversion.toBytes(call.getPrice().getAmount(),
                        PaymentRequestAvroModel.getClassSchema().getField("price").schema(),
                        PaymentRequestAvroModel.getClassSchema().getField("price").schema().getLogicalType()))
                .setPaymentStatus(PaymentStatus.PENDING)
                .setCreatedAt(domainEvent.getCreatedAt().toInstant())
                .build();
    }

    public DriverApprovalRequestAvroModel callPaidEventToDriverApprovalRequestAvroModel(CallPaidEvent domainEvent) {
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

    public PaymentResponseDto paymentResponseAvroToPaymentResponseDto(PaymentResponseAvroModel model) {
        return PaymentResponseDto.builder()
                .id(model.getId().toString())
                .sagaId(model.getSagaId().toString())
                .callId(model.getCallId().toString())
                .paymentId(model.getPaymentId().toString())
                .paymentStatus(PaymentStatus.valueOf(
                        model.getPaymentStatus().name()))
                .price(decimalConversion.fromBytes(model.getPrice(),
                        model.getSchema().getField("price").schema(),
                        model.getSchema().getField("price").schema().getLogicalType()))
                .createdAt(model.getCreatedAt())
                .failureMessages(model.getFailureMessages().toString())
                .build();
    }

    public DriverApprovalResponseDto driverApprovalResponseAvroToDriverResponseDto(DriverApprovalResponseAvroModel model) {
        return DriverApprovalResponseDto.builder()
                .id(model.getId().toString())
                .sagaId(model.getSagaId().toString())
                .driverId(model.getDriverId().toString())
                .callId(model.getCallId().toString())
                .driverApprovalStatus(DriverApprovalStatus.valueOf(
                        model.getDriverApprovalStatus().name()))
                .createdAt(model.getCreatedAt())
                .failureMessages(model.getFailureMessages().toString())
                .build();
    }
}
