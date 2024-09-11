package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.OrderApprovalEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Repository
public class R2DBCOrderApprovalRepository implements OrderApprovalRepository {

    private final DatabaseClient databaseClient;

    public R2DBCOrderApprovalRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<OrderApprovalEntity> findByOrderIdAndStatus(UUID orderId, String status) {
        String query = "SELECT * FROM order_approval " +
            "WHERE order_id = :orderId AND status = :status";

        return databaseClient.sql(query)
            .bind("orderId", orderId)
            .bind("status", status)
            .map(row -> new OrderApprovalEntity(
                row.get("id", UUID.class),
                row.get("order_id", UUID.class),
                row.get("restaurant_id", UUID.class),
                row.get("status", String.class)
            ))
            .one();
    }

    @Override
    public Mono<Void> upsert(OrderApprovalEntity entity) {
        String query = "INSERT INTO order_approval (id, order_id, restaurant_id, status) " +
            "VALUES (:id, :orderId, :restaurantId, :status) " +
            "ON CONFLICT (id) DO UPDATE SET " +
            "order_id = EXCLUDED.order_id, " +
            "restaurant_id = EXCLUDED.restaurant_id, " +
            "status = EXCLUDED.status";

        return databaseClient.sql(query)
            .bind("id", entity.getId())
            .bind("orderId", entity.getOrderId())
            .bind("restaurantId", entity.getRestaurantId())
            .bind("status", entity.getStatus())
            .fetch()// 쿼리 결과를 Mono, Flux 로 감싸서 가져옴.
            .rowsUpdated()// 영향을 받은 행 수를 반환
            .then(); //  이전 작업이 완료된 후 다음 작업을 수행
    }

    @Override
    public Mono<OrderApprovalEntity> save(OrderApprovalEntity entity) {
        return upsert(entity)
            .then(findByOrderIdAndStatus(entity.getOrderId(), entity.getStatus())
                .defaultIfEmpty(entity));
    }
}
