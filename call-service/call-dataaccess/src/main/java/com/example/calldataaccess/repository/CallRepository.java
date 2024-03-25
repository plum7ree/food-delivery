package com.example.calldataaccess.repository;

import com.example.calldataaccess.entity.CallEntity;
import com.example.calldomain.data.aggregate.Call;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CallRepository  extends JpaRepository<CallEntity, UUID> {

}
