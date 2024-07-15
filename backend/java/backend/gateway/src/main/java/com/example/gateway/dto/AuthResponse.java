package com.example.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class AuthResponse {
    private String userId;
    private String email;
    private String name;

    // constructor, getters and setters
}