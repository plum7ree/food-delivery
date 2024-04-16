package com.example.user.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/seller")
public class SellerController {

    @PostMapping("/restaurant-picture")
    public void uploadRestaurantPicture() {

    }

    @PostMapping("/register/restaurant")
    public void registerRestuarant() {
        // uploaded restaurant picture
        //TODO if user cancelled register restaurant, we should mark or delete an image.
    }

    @PostMapping("/register/{restaurantId}/menu")
    public void registerMenu(@PathVariable("restaurantId") String restaurantId) {

    }


}
