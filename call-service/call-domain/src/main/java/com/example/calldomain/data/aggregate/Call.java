package com.example.calldomain.data.aggregate;

import com.example.commondata.domain.aggregate.AggregateRoot;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

//@Data //TODO hashCode conflict 에러 뜨는데 AggregateRoot 거 사용하기.
@Builder
@RequiredArgsConstructor
public class Call extends AggregateRoot<CallId> {
    public void init() {
        setId(new CallId(UUID.randomUUID()));
    }


    public void pay() {
    }

}