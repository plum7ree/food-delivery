package com.example.authserver.data.dto.web;

import lombok.Builder;

@Builder
public record RefreshTokenRequestDto(String refreshToken) {
}
