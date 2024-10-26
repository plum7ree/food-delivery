package com.example.user.data.dto.web;

import lombok.Builder;

@Builder
public record RefreshTokenRequestDto(String refreshToken) {
}
