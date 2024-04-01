package com.example.gateway.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDto {
    private Long id;
    private String email;

    @JsonIgnore
    private String password;

    @JsonIgnore
    private String role;

    // Getters and setters

    // ...
}