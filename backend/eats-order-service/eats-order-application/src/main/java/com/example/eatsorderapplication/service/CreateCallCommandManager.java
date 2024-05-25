package com.example.eatsorderapplication.service;

import com.example.eatsorderdataaccess.repository.adapter.CallRepositoryAdapter;
import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.eatsorderdomain.data.dto.CreateEatsOrderCommandDto;
import com.example.eatsorderdomain.data.event.CallCreatedEvent;
import com.example.eatsorderdomain.data.mapper.DataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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
        var event = call.updateIdsAndCreateEvent();
        // save
        callRepositoryAdapter.save(call);


        return event;
    }

}
