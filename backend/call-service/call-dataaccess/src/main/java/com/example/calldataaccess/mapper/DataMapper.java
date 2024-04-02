package com.example.calldataaccess.mapper;

import com.example.calldataaccess.entity.CallEntity;
import com.example.calldomain.data.aggregate.Call;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.aggregate.valueobject.DriverId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.UserId;
import org.springframework.stereotype.Component;

@Component("callDataAccessMapper")
// Enttiy <-> Call (contains multiple ValueObject. CallId, DriverId, Money, ...)
public class DataMapper {

    public CallEntity callToCallEntity(Call call) {
        // CallId extends BaseId, BaseId contains UUID. getValue().
        return CallEntity.builder()
                .id(call.getId().getValue())
                .userId(call.getUserId().getValue())
                .driverId(call.getDriverId().getValue())
                .price(call.getPrice().getAmount())
                .callStatus(call.getCallStatus())
                .build();
    }

    public Call callEntityToCall(CallEntity callEntity) {

        return Call.builder()
                .Id(new CallId(callEntity.getId()))
                .userId(new UserId(callEntity.getUserId()))
                .driverId(new DriverId(callEntity.getDriverId()))
                .price(new Money(callEntity.getPrice()))
                .callStatus(callEntity.getCallStatus())
                .build();
    }

}
