package com.example.authserver.data.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    private UUID id;
    @NotNull
    @Column(name = "user_id")
    private UUID userId;
    @NotNull
    private String city;
    @NotNull
    private String street;
    @NotNull
    @Column(name = "postal_code")
    private String postalCode;
    @NotNull
    private Double lon;
    @NotNull
    private Double lat;
}