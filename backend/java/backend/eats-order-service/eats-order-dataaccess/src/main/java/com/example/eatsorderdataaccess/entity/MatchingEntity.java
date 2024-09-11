package com.example.eatsorderdataaccess.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "matching")
public class MatchingEntity {


    @Id
    @NotNull
    @Column(name = "id", columnDefinition = "uuid")
    private UUID id;
    @NotNull
    @Column(name = "user_id", columnDefinition = "uuid")
    private UUID userId;
    @NotNull
    @Column(name = "addressr_id", columnDefinition = "uuid")
    private UUID addressId;
    @NotNull
    @Column(name = "driver_id", columnDefinition = "uuid")
    private UUID driverId;
    @NotNull
    private String status; // PENDING, PROCESSING, COMPLETED, FAILED

}