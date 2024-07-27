package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

    @Modifying
    @Query(value = "INSERT INTO orders (id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages) " +
        "VALUES (:id, :customerId, :restaurantId, :trackingId, :price, cast(:orderStatus as order_status), :failureMessages)",
        nativeQuery = true)
    void saveWithCast(@Param("id") UUID id,
                      @Param("customerId") UUID customerId,
                      @Param("restaurantId") UUID restaurantId,
                      @Param("trackingId") UUID trackingId,
                      @Param("price") BigDecimal price,
                      @Param("orderStatus") String orderStatus,
                      @Param("failureMessages") String failureMessages);
}