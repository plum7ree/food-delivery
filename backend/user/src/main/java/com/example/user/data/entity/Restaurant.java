package com.example.user.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;


@Entity
@Getter
@Setter
@Builder
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @Column(nullable = false, unique = true)
    private String userId;

    private String name;
    private String type;

    @Column(columnDefinition = "TIME")
    @Temporal(TemporalType.TIME)
    private LocalTime openTime;

    @Column(columnDefinition = "TIME")
    @Temporal(TemporalType.TIME)
    private LocalTime closeTime;

    // private List<Menu>
    // address Address // address should be value class?
    // private List<Grade> grades
}