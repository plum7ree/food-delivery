package com.example.eatsorderdomain.data.event;

import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.commondata.domain.event.DomainEvent;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Data
@RequiredArgsConstructor
abstract class AbstractCallEvent implements DomainEvent<Call> {
    private final Call call;
    private final ZonedDateTime createdAt;

}
