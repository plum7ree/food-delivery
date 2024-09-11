package com.example.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    // 주의: Generated Value 를 사용하면 Dto 에서 id 를 넘겨줄수가없다. 다른 값을 생성해버림.
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

    @Column(name = "created_at", nullable = false, updatable = false)
    @JsonIgnore
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    @JsonIgnore
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // fetch Lazy restaurant + avoid N + 1?
    // or seperate into another table?
//    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, orphanRemoval=true)
//    @BatchSize(size = 10)
//    private List<Restaurant> restaurants;

}