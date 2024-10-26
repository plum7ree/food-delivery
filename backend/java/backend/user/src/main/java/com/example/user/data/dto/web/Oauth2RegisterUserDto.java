package com.example.user.data.dto.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Oauth2RegisterUserDto {
    private String lat;
    private String lon;
    private String city;
    private String street;
    private String postalCode;
    private String username;
    // Getters and setters

    // ...
}