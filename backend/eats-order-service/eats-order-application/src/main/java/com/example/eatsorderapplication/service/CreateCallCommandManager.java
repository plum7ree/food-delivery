package com.example.eatsorderapplication.service;

import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.aggregate.valueobject.CallStatus;
import com.example.commondata.domain.aggregate.valueobject.TrackingId;
import com.example.eatsorderdataaccess.repository.adapter.CallRepositoryAdapter;
import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.eatsorderdomain.data.dto.CreateEatsOrderCommandDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.eatsorderdomain.data.mapper.DataMapper;
import com.example.kafka.avro.model.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateCallCommandManager {
    private final DataMapper dataMapper;

    private final CallRepositoryAdapter callRepositoryAdapter;

    @Transactional
    public CallCreatedEvent createCallTransaction(CreateEatsOrderCommandDto createEatsOrderCommandDto) {
        // check User valid
        // check Driver valid
        // map
        Call call = dataMapper.createCallCommandDtoToCall(createEatsOrderCommandDto);
        // check call element (price > 0, callstatus, ) valid.
        call.validateCall();
        // create new CallId, new TrackingId
        call.setId(new CallId(UUID.randomUUID()));
        call.setTrackingId(new TrackingId(UUID.randomUUID()));
        call.setCallStatus(CallStatus.PENDING);
        call.setStatus(Status.PENDING);
        var event =  new CallCreatedEvent(call, ZonedDateTime.now(ZoneId.of("UTC")));
        // save
        callRepositoryAdapter.save(call);


        return event;
    }

}
