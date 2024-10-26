package com.example.user.data.dto.web;

import lombok.Builder;

@Builder
public record RefreshTokenResponseDto(String accessToken, String message) {
}
