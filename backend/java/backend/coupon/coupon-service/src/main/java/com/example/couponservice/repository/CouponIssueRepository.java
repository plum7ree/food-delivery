package com.example.couponservice.repository;

import com.example.couponservice.entity.Coupon;
import com.example.couponservice.entity.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponIssueRepository extends JpaRepository<CouponIssue, Long> {
}
