package com.example.calldomain.data.aggregate;

import com.example.commondata.domain.aggregate.AggregateRoot;
import com.example.commondata.domain.aggregate.Payment;
import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.kafka.avro.model.PaymentStatus;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

//@Data //TODO hashCode conflict 에러 뜨는데 AggregateRoot 거 사용하기.

public class Call extends AggregateRoot<CallId> {


    private  UserId userId;
    private  DriverId driverId;
    private  Money price;


    public void updateStateToPaid() {
    }

    public void updateStateToRejected() {
    }

    public void updateStateToApproved() {
    }

         private Call(Call.Builder builder) {
        setId(builder.callId);
        driverId = builder.driverId;
        userId = builder.userId;
        price = builder.price;
    }

        public static Builder builder() {
        return new Call.Builder();
    }


    public static final class Builder {
        private CallId callId;
    private  UserId userId;
    private  DriverId driverId;
    private  Money price;

        private Builder() {
        }

        public Builder callId(CallId val) {
            callId = val;
            return this;
        }


        public Builder userId(UserId val) {
            userId = val;
            return this;
        }

        public Builder driverId(DriverId val) {
            driverId = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }


        public Call build() {
            return new Call(this);
        }
    }
}