package com.example.user.data.repository;

import com.example.user.data.entity.Restaurant;
import com.example.user.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, String> {

}
