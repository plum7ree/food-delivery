package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.entity.QOrderEntity;
import com.example.eatsorderdataaccess.entity.QRestaurantApprovalRequestEntity;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalRequestEntity;
import com.example.eatsorderdataaccess.repository.jpa.QueryDSLOrderRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

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
        QOrderEntity qOrderEntity = new QOrderEntity("orders");
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
    public void insertRestaurantApproval(RestaurantApprovalRequestEntity entity) {
        QRestaurantApprovalRequestEntity qRestaurantApprovalRequestEntity = QRestaurantApprovalRequestEntity.restaurantApprovalRequestEntity;
        queryFactory.insert(qRestaurantApprovalRequestEntity)
            .set(qRestaurantApprovalRequestEntity.id, entity.getId())
            .set(qRestaurantApprovalRequestEntity.sagaId, entity.getSagaId())
            .set(qRestaurantApprovalRequestEntity.createdAt, entity.getCreatedAt())
            .set(qRestaurantApprovalRequestEntity.processedAt, entity.getProcessedAt())
            .set(qRestaurantApprovalRequestEntity.type, entity.getType())
            .set(qRestaurantApprovalRequestEntity.payload, entity.getPayload())
            .set(qRestaurantApprovalRequestEntity.orderStatus, entity.getOrderStatus())
            .set(qRestaurantApprovalRequestEntity.outboxStatus, entity.getOutboxStatus())
            .set(qRestaurantApprovalRequestEntity.sagaStatus, entity.getSagaStatus())
            .set(qRestaurantApprovalRequestEntity.version, entity.getVersion())
            .execute();
    }
}
