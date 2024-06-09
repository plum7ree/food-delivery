package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.CallEntity;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface QueryDSLCallRepository {

    // parameter name: limit, pageSize, count automatically calls setMaxResults(limit)
    // offset calls setFirstResult(offset)
    List<CallEntity> findCallEntitiesByUserId(@Param("userID") UUID userID, int limit);


}
