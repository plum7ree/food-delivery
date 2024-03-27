package com.example.callapplication.service;

import com.example.calldataaccess.repository.adapter.CallRepositoryAdapter;
import com.example.calldomain.data.aggregate.Call;
import com.example.calldomain.data.dto.CreateCallCommandDto;
import com.example.calldomain.data.event.CallCreatedEvent;
import com.example.calldomain.data.mapper.DataMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CreateCallCommandManager {
    private final DataMapper dataMapper;

    private final CallRepositoryAdapter callRepositoryAdapter;

    @Transactional
    public CallCreatedEvent createCallTransaction(CreateCallCommandDto createCallCommandDto) {
        // check User valid
        // check Driver valid
        // map
        Call call = dataMapper.createCallCommandDtoToCall(createCallCommandDto);
        // check call element (price > 0, callstatus, ) valid.
        call.validateCall();
        // create new CallId, new TrackingId
        var event = call.updateIdsAndCreateEvent();
        // save
        callRepositoryAdapter.save(call);


        return event;
    }

}
