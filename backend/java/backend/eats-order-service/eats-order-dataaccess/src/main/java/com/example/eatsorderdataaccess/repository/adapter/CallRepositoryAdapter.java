package com.example.eatsorderdataaccess.repository.adapter;

import com.example.eatsorderdataaccess.mapper.DataMapper;
import com.example.eatsorderdataaccess.repository.CallRepository;
import com.example.eatsorderdomain.data.aggregate.Call;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CallRepositoryAdapter {

    private final CallRepository callRepository;
    private final DataMapper dataMapper;

    public CallRepositoryAdapter(CallRepository callRepository, DataMapper dataMapper) {
        this.callRepository = callRepository;
        this.dataMapper = dataMapper;
    }

    public Optional<Call> findById(CallId callId) {
        return callRepository.findById(callId.getValue())
                .map(dataMapper::callEntityToCall);
    }

    public Call save(Call val) {

        var entity = dataMapper.callToCallEntity(val);
        callRepository.saveWithCast(entity.getId(),
                entity.getUserId(),
                entity.getDriverId(),
                entity.getPrice(),
                entity.getCallStatus().toString(),
                entity.getFailureMessages());
        return val;
    }


}
