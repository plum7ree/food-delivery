package com.example.restaurant.data.entity;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @OneToMany(mappedBy = "menu")
    List<RestaurantMenuImd> restaurantMenuImdList = new ArrayList<>();


}

