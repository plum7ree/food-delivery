package com.example.user.data.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * {
 * userName: "이**",
 * rating: 5,
 * date: "1일 전",
 * image: "https://example.com/burger-image.jpg",
 * comment: "모든메뉴가 너무 맛있고 퀄리티좋아요!"
 * },
 */

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @JoinColumn(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "user_name", nullable = false)
    private String userName;

    @JoinColumn(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(nullable = false)
    private Float rating;

    @Column(nullable = false)
    private LocalDateTime created_at;

    @Column(nullable = false)
    private LocalDateTime updated_at;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(length = 1000)
    private String comment;
}