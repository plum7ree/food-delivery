package com.example.eatsorderdomain.data.aggregate;

import com.example.commondata.domain.aggregate.AggregateRoot;
import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.kafka.avro.model.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

//@Data //TODO hashCode conflict 에러 뜨는데 AggregateRoot 거 사용하기.
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class Call extends AggregateRoot<CallId> {

    // private final: these are immutable.
    private CallerId callerId;
    private CalleeId calleeId;
    private Money price;

    // private: these are mutable.
    private TrackingId trackingId;
    private CallStatus callStatus;
    private Status status;

//
    public void updateCallStatus(CallStatus status) {
        callStatus = status;
    }


    public void validateCall() {

    }
}