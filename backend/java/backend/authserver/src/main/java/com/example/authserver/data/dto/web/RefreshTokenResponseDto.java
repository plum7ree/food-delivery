package com.example.authserver.data.dto.web;

import lombok.Builder;

@Builder
public record RefreshTokenResponseDto(String accessToken, String message) {
}
