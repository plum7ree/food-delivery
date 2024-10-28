package com.example.authserver.data.dto.web;

import lombok.Builder;

@Builder
public record LoginResponseDto(String accessToken, String refreshToken) {
}
