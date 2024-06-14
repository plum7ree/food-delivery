package com.example.eatsorderdataaccess.repository.jdbc;

import com.example.commondata.domain.aggregate.valueobject.OrderStatus;
import com.example.commondata.domain.aggregate.valueobject.OutboxStatus;
import com.example.commondata.domain.aggregate.valueobject.SagaStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;
import java.time.ZonedDateTime;
import java.sql.Timestamp;

@Repository
public class OrderAppDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OrderAppDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insertOrder(UUID id, UUID customerId, UUID restaurantId, UUID trackingId, BigDecimal price, OrderStatus orderStatus, String failureMessages) {
        String sql = "INSERT INTO orders (id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages) " +
            "VALUES (?, ?, ?, ?, ?, ?::order_status, ?)";
        jdbcTemplate.update(sql, id, customerId, restaurantId, trackingId, price, orderStatus.toString(), failureMessages);
    }

    public void insertRestaurantApproval(UUID id, UUID sagaId, ZonedDateTime createdAt, ZonedDateTime processedAt, String type, String payload, OrderStatus orderStatus, OutboxStatus outboxStatus, SagaStatus sagaStatus, int version) {
        String sql = "INSERT INTO restaurant_approval_outbox (id, saga_id, created_at, processed_at, type, payload, order_status, outbox_status, saga_status, version) " +
            "VALUES (?, ?, ?, ?, ?, ?::jsonb, ?::order_status, ?::outbox_status, ?::saga_status, ?)";
        jdbcTemplate.update(sql, id, sagaId, Timestamp.from(createdAt.toInstant()), Timestamp.from(processedAt.toInstant()), type, payload, orderStatus.toString(), outboxStatus.toString(), sagaStatus.toString(), version);
    }
}