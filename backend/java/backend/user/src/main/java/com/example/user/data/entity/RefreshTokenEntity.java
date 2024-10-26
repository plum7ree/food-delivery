package com.example.user.data.entity;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record RefreshTokenEntity(
    String email,
    String value,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}
