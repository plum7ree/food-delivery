package com.example.restaurant.data.entity;


import com.example.commondata.entity.BaseTimeValue;
import jakarta.persistence.*;

/**
 * Intermediate Table between
 * OneToMany relationship between restaurant and menu
 */
@Entity
public class RestaurantMenuImd {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne
    @JoinColumn(name="restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name="menu_id")
    private Menu menu;

    @Embedded
    private BaseTimeValue time;

}
