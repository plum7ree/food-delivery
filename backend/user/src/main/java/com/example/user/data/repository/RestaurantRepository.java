package com.example.user.data.repository;

import com.example.user.data.dto.RestaurantTypeEnum;
import com.example.user.data.entity.Restaurant;
import com.example.user.data.entity.Restaurant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {

    Page<Restaurant> findByType(RestaurantTypeEnum typeEnum, Pageable pageable);

}
