package com.example.eatsorderdataaccess.repository;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.entity.QOrderEntity;
import com.example.eatsorderdataaccess.entity.QRestaurantApprovalRequestEntity;
import com.example.eatsorderdataaccess.repository.jpa.QueryDSLOrderRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public class QueryDSLOrderRepositoryImpl implements QueryDSLOrderRepository {
    private final JPAQueryFactory queryFactory;

    public QueryDSLOrderRepositoryImpl(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    // parameter name: limit, pageSize, count automatically calls setMaxResults(limit)
    // offset calls setFirstResult(offset)
    @Override
    public List<OrderEntity> findOrderEntitiesByUserId(UUID customerId, int limit) {
        QOrderEntity orderEntity = QOrderEntity.orderEntity;
        return queryFactory.selectFrom(orderEntity)
            .where(orderEntity.customerId.eq(customerId))
            .orderBy(orderEntity.id.desc())
            .limit(limit)
            .fetch();
    }

    @Override
    public void insertOrder(OrderEntity orderEntity) {
        QOrderEntity qOrderEntity = QOrderEntity.orderEntity;
        queryFactory.insert(qOrderEntity)
            .set(qOrderEntity.id, orderEntity.getId())
            .set(qOrderEntity.customerId, orderEntity.getCustomerId())
            .set(qOrderEntity.restaurantId, orderEntity.getRestaurantId())
            .set(qOrderEntity.trackingId, orderEntity.getTrackingId())
            .set(qOrderEntity.price, orderEntity.getPrice())
            .set(qOrderEntity.orderStatus, orderEntity.getOrderStatus())
            .set(qOrderEntity.failureMessages, orderEntity.getFailureMessages())
            .execute();
    }

    @Override
    public void insertRestaurantApproval(UUID id, UUID sagaId,
                                         ZonedDateTime createdAt,
                                         ZonedDateTime processedAt,
                                         String type,
                                         String payload,
                                         OrderStatus orderStatus,
                                         OutboxStatus outboxStatus,
                                         SagaStatus sagaStatus,
                                         int version) {
        QRestaurantApprovalRequestEntity qRestaurantApprovalRequestEntity = QRestaurantApprovalRequestEntity.restaurantApprovalRequestEntity;
        queryFactory.insert(qRestaurantApprovalRequestEntity)
            .set(qRestaurantApprovalRequestEntity.id, id)
            .set(qRestaurantApprovalRequestEntity.sagaId, sagaId)
            .set(qRestaurantApprovalRequestEntity.createdAt, createdAt)
            .set(qRestaurantApprovalRequestEntity.processedAt, processedAt)
            .set(qRestaurantApprovalRequestEntity.type, type)
            .set(qRestaurantApprovalRequestEntity.payload, payload)
            .set(qRestaurantApprovalRequestEntity.orderStatus, orderStatus)
            .set(qRestaurantApprovalRequestEntity.outboxStatus, outboxStatus)
            .set(qRestaurantApprovalRequestEntity.sagaStatus, sagaStatus)
            .set(qRestaurantApprovalRequestEntity.version, version)
            .execute();
    }
}
