package com.example.user.data.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private String email;
    private String username;
    private String profilePicUrl;
    @JsonIgnore
    private String password;

    @JsonIgnore
    private String role;

    @JsonIgnore
    private String oauth2Provider;
    @JsonIgnore
    private String oauth2Sub;

    // Getters and setters

    // ...
}