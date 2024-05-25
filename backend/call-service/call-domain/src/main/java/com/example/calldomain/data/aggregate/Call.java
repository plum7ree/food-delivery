package com.example.calldomain.data.aggregate;

import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.commondata.domain.aggregate.AggregateRootV1;
import com.example.commondata.domain.aggregate.valueobject.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

//@Data //TODO hashCode conflict 에러 뜨는데 AggregateRoot 거 사용하기.
@Getter
@Setter
@Slf4j
public class Call extends AggregateRootV1<CallId> {

    // private final: these are immutable.
    private final UserId userId;
    private final DriverId driverId;
    private final Money price;

    // private: these are mutable.
    private TrackingId trackingId;
    private CallStatus callStatus;


    private Call(Call.Builder builder) {
        setId(builder.callId);
        driverId = builder.driverId;
        userId = builder.userId;
        price = builder.price;
        trackingId = builder.trackingId;
        callStatus = builder.callStatus;
    }

    public static Builder builder() {
        return new Call.Builder();
    }

    public void updateStatusToPaid() {
        callStatus = CallStatus.PAID;
    }

    public void updateStatusToRejected() {
    }

    public void updateStatusToApproved() {
    }

    public void validateCall() {

    }

    public CallCreatedEvent updateIdsAndCreateEvent() {
        setId(new CallId(UUID.randomUUID()));
        setTrackingId(new TrackingId(UUID.randomUUID()));
        setCallStatus(CallStatus.PENDING);
        return new CallCreatedEvent(this, ZonedDateTime.now(ZoneId.of("UTC")));
    }


    public static final class Builder {
        private CallId callId;
        private UserId userId;
        private DriverId driverId;
        private Money price;

        private TrackingId trackingId;
        private CallStatus callStatus;

        private Builder() {
        }

        public Builder Id(CallId val) {
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

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder callStatus(CallStatus val) {
            callStatus = val;
            return this;
        }

        public Call build() {
            return new Call(this);
        }
    }
}