package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.OrderEntity;
import com.example.eatsorderdataaccess.entity.OrderOutboxEntity;
import com.example.eatsorderdomain.data.domainentity.Order;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface OrderRepository {

    //    // ref: https://hstory0208.tistory.com/entry/JPA-Modifying%EC%9D%B4%EB%9E%80-%EA%B7%B8%EB%A6%AC%EA%B3%A0-%EC%A3%BC%EC%9D%98%ED%95%A0%EC%A0%90-%EB%B2%8C%ED%81%AC-%EC%97%B0%EC%82%B0
//    // 벌크 연산 수행하므로 영속성 컨텍스트를 거치지 않고 db 직접 변경한다
//    // 따라서 조회시 영속성 컨텍스트는 업데이트 되어있는 상태가 아니다.
//    // 따라서 clearAutomatically 옵션으로 1차 캐시 날려줘야함.
//    @Modifying(clearAutomatically = true)
//    @Query(value = "INSERT INTO orders (id, customer_id, restaurant_id, tracking_id, price, order_status, failure_messages) " +
//        "VALUES (:id, :customerId, :restaurantId, :trackingId, :price, :orderStatus, :failureMessages)",
//        nativeQuery = true)
    Mono<OrderEntity> save(OrderEntity order);

    Mono<Long> updateStatus(UUID id, String orderStatus);

    Mono<Order> findById(UUID id);


}