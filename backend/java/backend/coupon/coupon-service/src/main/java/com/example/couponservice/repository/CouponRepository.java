package com.example.couponservice.repository;

import com.example.couponservice.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Modifying
    @Query(value = "UPDATE coupons SET issued_quantity = issued_quantity + 1 " +
        "WHERE id = :#{#coupon.id} AND issued_quantity < max_quantity",
        nativeQuery = true)
    int incrementIssuedQuantityNativeWithCheck(@Param("coupon") Coupon coupon);


//    문제: 같은 서비스 여려개가 db 에 이 쿼리를 날리면 안전한가?
//    만일 update 한 후 maxCount 보다 커지면 어떡한가?
//
//    애초에 db 가 한번에 하나의 요청 밖에 처리 안하나?
//    동시성, 원자성, 일관성, ...
//    optimistic lock
//    유저끼리 송금하는 기능 도 만들기
//    https://velog.io/@combi_jihoon/%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%B2%A0%EC%9D%B4%EC%8A%A4-DB-Concurrency


}
