package com.example.calldomain.data.mapper;

import com.example.calldomain.data.aggregate.Call;
import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.calldomain.data.dto.DriverApprovalResponseDto;
import com.example.calldomain.data.dto.PaymentResponseDto;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.calldomain.data.event.CallPaidEvent;
import com.example.kafka.avro.model.DriverApprovalRequestAvroModel;
import com.example.kafka.avro.model.DriverApprovalResponseAvroModel;
import com.example.kafka.avro.model.PaymentRequestAvroModel;
import com.example.kafka.avro.model.PaymentResponseAvroModel;
import org.springframework.stereotype.Component;

@Component("callDomainDataMapper")
public class DataMapper {
    public Call createCallCommandDtoToCall(CreateCallCommandDto createCallCommandDto) {
        return Call.builder().build();
    }

    public PaymentRequestAvroModel callCreatedEventToPaymentRequestAvroModel(CallCreatedEvent domainEvent) {
        return PaymentRequestAvroModel.newBuilder()
                .build();
    }
    public DriverApprovalRequestAvroModel callPaidEventToDriverApprovalRequestAvroModel(CallPaidEvent domainEvent) {
        return DriverApprovalRequestAvroModel.newBuilder()
                .build();
    }
    public PaymentResponseDto paymentResponseAvroToPaymentResponseDto(PaymentResponseAvroModel model ) {
        return PaymentResponseDto.builder().build();
    }

    public DriverApprovalResponseDto driverApprovalResponseAvroToDriverResponseDto(DriverApprovalResponseAvroModel responseAvroModel) {
        return DriverApprovalResponseDto.builder().build();
    }
}
