package com.example.restaurant.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;
import java.util.List;


@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "restaurants")
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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

    @OneToMany(mappedBy = "restaurant")
    private List<RestaurantMenuImd> restaurantMenuImd;


}

// mapped super class
// table per class