package com.example.login.data.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;


@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String username;

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    // OAuth2 provider
    private String provider;
    // OAuth2 providerId
    private String providerId;

    @Column(nullable = false)
    @JsonIgnore
    private String role;
    // Getters and Setters

    @Column(nullable = false)
    private Boolean isRegistrationComplete;

    // fetch Lazy restaurant + avoid N + 1?
    // or seperate into another table?
//    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, orphanRemoval=true)
//    @BatchSize(size = 10)
//    private List<Restaurant> restaurants;

}