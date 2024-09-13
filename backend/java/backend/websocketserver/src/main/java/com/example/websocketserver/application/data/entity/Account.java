package com.example.websocketserver.application.data.entity;


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

    @Column(nullable = false, unique = true)
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String role;
    // Getters and Setters

    private String oauth2Provider;
    private String oauth2Sub;

    // fetch Lazy restaurant + avoid N + 1?
    // or seperate into another table?
//    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, orphanRemoval=true)
//    @BatchSize(size = 10)
//    private List<Restaurant> restaurants;

}