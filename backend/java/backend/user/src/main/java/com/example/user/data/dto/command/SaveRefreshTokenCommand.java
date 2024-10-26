package com.example.user.data.dto.command;

import lombok.Builder;

@Builder
public record SaveRefreshTokenCommand(String email, String token) {

}
