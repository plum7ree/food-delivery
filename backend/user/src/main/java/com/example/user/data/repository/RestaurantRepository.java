package com.example.user.data.repository;

import com.example.user.data.entity.Restaurant;
import com.example.user.data.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

}
