package com.example.user.data.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "restaurant_like")  // 'like'는 SQL 예약어이므로 테이블명 변경
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;  // 실제로는 User 엔티티를 참조하는 것이 좋습니다.

    // getters, setters
}