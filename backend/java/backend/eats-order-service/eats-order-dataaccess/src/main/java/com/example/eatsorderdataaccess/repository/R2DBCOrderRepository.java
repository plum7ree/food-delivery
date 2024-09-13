package com.example.eatsorderdataaccess.repository;

import com.example.commondata.domain.events.order.OrderStatus;
import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.entity.OrderOutboxEntity;
import com.example.eatsorderdomain.data.domainentity.Order;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class R2DBCOrderRepository implements OrderRepository {

    private final DatabaseClient databaseClient;

    public R2DBCOrderRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Mono<OrderEntity> save(OrderEntity orderEntity) {

        String query = "INSERT INTO orders (id, customer_id, restaurant_id, price, order_status, failure_messages) " +
            "VALUES (:id, :customerId, :restaurantId, :price, :orderStatus, :failureMessages)";

        return databaseClient.sql(query)
            .bind("id", orderEntity.getId())
            .bind("customerId", orderEntity.getCustomerId())
            .bind("restaurantId", orderEntity.getRestaurantId())
            .bind("price", orderEntity.getPrice())
            .bind("orderStatus", orderEntity.getOrderStatus())
            .bind("failureMessages", orderEntity.getFailureMessages())
            .fetch()
            .rowsUpdated().thenReturn(orderEntity);
    }

    @Override
    public Mono<Long> updateStatus(UUID id, String orderStatus) {

        String query = "UPDATE orders SET order_status = :orderStatus WHERE id = :id";

        return databaseClient.sql(query)
            .bind("id", id)
            .bind("orderStatus", orderStatus)
            .fetch()
            .rowsUpdated();
    }


    @Override
    public Mono<Order> findById(UUID id) {
        String query = "SELECT id, customer_id, restaurant_id, price, order_status, failure_messages " +
            "FROM orders WHERE id = :id";

        return databaseClient.sql(query)
            .bind("id", id)
            .map(row -> {

                    var order = Order.builder()
                        .id(row.get("id", UUID.class))
                        .callerId(row.get("customer_id", UUID.class))
                        .calleeId(row.get("restaurant_id", UUID.class))
                        .price(row.get("price", Double.class))
                        .orderStatus(OrderStatus.valueOf(row.get("order_status", String.class)))
                        .build();
                    return order;
                }
            )
            .one();  // Retrieve a single row
    }

}
