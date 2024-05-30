package com.example.commondata.domain.aggregate;

import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.aggregate.valueobject.DriverApprovalId;
import com.example.commondata.domain.aggregate.valueobject.CalleeId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.kafka.avro.model.DriverApprovalStatus;
import lombok.Getter;

@Getter
public class DriverApproval extends AggregateRootV1<DriverApprovalId> {
    private CallId callId;
    private CalleeId calleeId;
    private Money price;
    private DriverApprovalStatus status;

    private DriverApproval(Builder builder) {
        setId(builder.id);
        callId = builder.callId;
        calleeId = builder.calleeId;
        price = builder.price;
        status = builder.status;
    }

    public static Builder builder() {
        return new Builder();
    }


    public static final class Builder {
        private DriverApprovalId id;
        private CallId callId;
        private CalleeId calleeId;
        private Money price;
        private DriverApprovalStatus status;

        private Builder() {
        }

        public Builder id(DriverApprovalId val) {
            id = val;
            return this;
        }

        public Builder driverId(CalleeId val) {
            calleeId = val;
            return this;
        }

        public Builder callId(CallId val) {
            callId = val;
            return this;
        }

        public Builder price(Money val) {
            price = val;
            return this;
        }

        public Builder driverApprovalStatus(DriverApprovalStatus val) {
            status = val;
            return this;
        }


        public DriverApproval build() {
            return new DriverApproval(this);
        }
    }
}
