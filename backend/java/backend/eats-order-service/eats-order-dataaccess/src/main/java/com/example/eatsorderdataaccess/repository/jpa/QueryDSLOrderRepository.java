package com.example.eatsorderdataaccess.repository.jpa;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalRequestEntity;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

public interface QueryDSLOrderRepository {

    // parameter name: limit, pageSize, count automatically calls setMaxResults(limit)
    // offset calls setFirstResult(offset)
    List<OrderEntity> findOrderEntitiesByUserId(@Param("userID") UUID userID, int limit);


    void insertOrder(OrderEntity orderEntity);


    void insertRestaurantApproval(RestaurantApprovalRequestEntity entity);
}
