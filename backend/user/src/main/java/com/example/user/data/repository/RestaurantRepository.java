package com.example.user.data.repository;

import com.example.user.data.dto.RestaurantDto;
import com.example.user.data.dto.RestaurantTypeEnum;
import com.example.user.data.entity.QRestaurant;
import com.example.user.data.entity.Restaurant;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

//public interface RestaurantRepository extends JpaRepository<Restaurant, UUID> {
//
//    Page<Restaurant> findByType(RestaurantTypeEnum typeEnum, Pageable pageable);
//
//}

@Repository
@Transactional
@Slf4j
public class RestaurantRepository {
    @Autowired
    EntityManager em;

	@Autowired
	private final JPAQueryFactory jpaQueryFactory;

	public RestaurantRepository(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	public Optional<Restaurant> findById(UUID id) {
		Restaurant restaurant = em.find(Restaurant.class, id);
		log.info("restaurant -> {}", restaurant);
		return Optional.of(restaurant);
	}

	public Restaurant save(Restaurant restaurant) {
		if (restaurant.getId() == null) {
			em.persist(restaurant);
		} else {
			em.merge(restaurant);
		}
		return restaurant;
	}

public Page<RestaurantDto> findByType(RestaurantTypeEnum type, Pageable pageable) {
    QRestaurant restaurant = QRestaurant.restaurant;

	//TODO instead of count entire db, how about make a recomendation system (memory or disk)?
    long total = jpaQueryFactory.select(restaurant)
        .from(restaurant)
        .where(restaurant.type.eq(type))
        .fetchCount();

    List<RestaurantDto> restaurantDtos = jpaQueryFactory.select(Projections.fields(
            RestaurantDto.class,
            restaurant.name,
            restaurant.type,
            restaurant.openTime,
            restaurant.closeTime
        ))
        .from(restaurant)
        .where(restaurant.type.eq(type))
        .offset((int) pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch();

    return new PageImpl<>(restaurantDtos, pageable, total);
}

public List<RestaurantDto> findAll() {
    QRestaurant restaurant = QRestaurant.restaurant;

    List<RestaurantDto> restaurantDtos = jpaQueryFactory.select(Projections.fields(
            RestaurantDto.class,
            restaurant.name,
            restaurant.type,
            restaurant.openTime,
            restaurant.closeTime
        ))
        .from(restaurant)
        .fetch();

    return restaurantDtos;
}

}