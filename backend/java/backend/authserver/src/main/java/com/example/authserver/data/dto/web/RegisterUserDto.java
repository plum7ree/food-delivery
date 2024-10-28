package com.example.authserver.data.dto.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {
    private String lat;
    private String lon;
    private String city;
    private String street;
    private String postalCode;
    private String email;
    private String profile_pic_url;
    private String username;
    private String password;
    // Getters and setters

    // ...
}