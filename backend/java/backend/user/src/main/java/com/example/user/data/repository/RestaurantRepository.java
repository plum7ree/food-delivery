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
    private final JPAQueryFactory jpaQueryFactory;
    @Autowired
    EntityManager em;

    public RestaurantRepository(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Optional<RestaurantDto> findById(UUID id) {
        QRestaurant restaurant = QRestaurant.restaurant;
        return Optional.ofNullable(jpaQueryFactory.select(Projections.fields(
                RestaurantDto.class,
                restaurant.id,
                restaurant.name,
                restaurant.type,
                restaurant.openTime,
                restaurant.closeTime,
                restaurant.pictureUrl1
            ))
            .from(restaurant)
            .where(restaurant.id.eq(id))
            .fetchOne());
    }

    public Restaurant save(Restaurant restaurant) {
        if (restaurant.getId() == null) {
            em.persist(restaurant);
        } else {
            em.merge(restaurant);
        }
        return restaurant;
    }

    public Page<RestaurantDto> findListByType(RestaurantTypeEnum type, Pageable pageable) {
        QRestaurant restaurant = QRestaurant.restaurant;

        List<RestaurantDto> restaurantDtos = jpaQueryFactory.select(Projections.fields(
                RestaurantDto.class,
                restaurant.id,
                restaurant.name,
                restaurant.type,
                restaurant.openTime,
                restaurant.closeTime,
                restaurant.pictureUrl1,
                restaurant.pictureUrl2,
                restaurant.pictureUrl3
            ))
            .from(restaurant)
            .where(restaurant.type.eq(type))
            .offset((int) pageable.getOffset())
            .limit(pageable.getPageSize() + 1)  // 추가로 한 개 더 가져와서 다음 페이지 여부 판단
            .fetch();

        boolean hasNext = false;
        if (restaurantDtos.size() > pageable.getPageSize()) {
            hasNext = true;
            restaurantDtos.remove(restaurantDtos.size() - 1);  // 추가로 가져온 데이터는 삭제
        }

        // total을 추정하거나 계산하지 않으므로 offset + 실제 가져온 데이터 수를 활용
        long total = hasNext ? pageable.getOffset() + pageable.getPageSize() + 1 : pageable.getOffset() + restaurantDtos.size();

        return new PageImpl<>(restaurantDtos, pageable, total);
    }

    public List<RestaurantDto> findAll() {
        QRestaurant restaurant = QRestaurant.restaurant;

        List<RestaurantDto> restaurantDtos = jpaQueryFactory.select(Projections.fields(
                RestaurantDto.class,
                restaurant.name,
                restaurant.type,
                restaurant.openTime,
                restaurant.closeTime,
                restaurant.pictureUrl1
            ))
            .from(restaurant)
            .fetch();

        return restaurantDtos;
    }

}