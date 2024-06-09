package com.example.eatsorderdataaccess.mapper;

import com.example.eatsorderdataaccess.entity.CallEntity;
import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.aggregate.valueobject.CalleeId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.commondata.domain.aggregate.valueobject.CallerId;
import org.springframework.stereotype.Component;

@Component("callDataAccessMapper")
// Enttiy <-> Call (contains multiple ValueObject. CallId, DriverId, Money, ...)
public class DataMapper {

    public CallEntity callToCallEntity(Call call) {
        // CallId extends BaseId, BaseId contains UUID. getValue().
        return CallEntity.builder()
                .id(call.getId().getValue())
                .userId(call.getCallerId().getValue())
                .driverId(call.getCalleeId().getValue())
                .price(call.getPrice().getAmount())
                .callStatus(call.getCallStatus())
                .build();
    }


    public Call callEntityToCall(CallEntity callEntity) {
        return Call.builder()
                .id(new CallId(callEntity.getId()))
                .callerId(new CallerId(callEntity.getUserId()))
                .calleeId(new CalleeId(callEntity.getDriverId()))
                .price(new Money(callEntity.getPrice()))
                .callStatus(callEntity.getCallStatus())
                .build();
    }
}
