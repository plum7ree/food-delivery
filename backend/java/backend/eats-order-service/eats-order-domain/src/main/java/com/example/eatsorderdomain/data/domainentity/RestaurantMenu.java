package com.example.eatsorderdomain.data.domainentity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class RestaurantMenu {
    private UUID id;
    private String name;
    private Double price;
    private List<RestaurantMenuOption> restaurantMenuOptions;

}
