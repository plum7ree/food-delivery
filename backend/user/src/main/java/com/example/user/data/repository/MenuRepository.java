package com.example.user.data.repository;

import com.example.user.data.dto.MenuDto;
import com.example.user.data.entity.Menu;
import com.example.user.data.entity.QMenu;
import com.example.user.data.entity.QRestaurant;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hibernate.query.results.Builders.fetch;


//public interface MenuRepository extends JpaRepository<Menu, UUID> {
//}

@Repository
@Transactional
@Slf4j
public class MenuRepository {

    @Autowired
    private final JPAQueryFactory jpaQueryFactory;
    @Autowired
    private final EntityManager em;

    public MenuRepository(JPAQueryFactory jpaQueryFactory, EntityManager em) {
        this.jpaQueryFactory = jpaQueryFactory;
        this.em = em;
    }

    public Optional<List<MenuDto>> findByRestaurantId(String restaurantId) {
        QMenu menu = QMenu.menu;
        QRestaurant restaurant = QRestaurant.restaurant;

        List<MenuDto> menuDtos = jpaQueryFactory.select(Projections.fields(
                        MenuDto.class,
                        menu.id,
                        //NOTE MenuDto.java : private UUID restaurant
                        //     Menu.java    : @JoinColumn(name = "restaurant_id") private Restaurant restaurant;
                        menu.restaurant.id.as("restaurantId"),
                        menu.name,
                        menu.description,
                        menu.pictureUrl,
                        menu.price
                )).from(menu)
                .where(menu.restaurant.id.eq(UUID.fromString(restaurantId)))
                .fetch();

        return Optional.of(menuDtos);
    }
    public Optional<List<MenuDto>> findByRestaurantIdNoDtoProjection(String restaurantId) {
        QMenu menu = QMenu.menu;
        var entityList = jpaQueryFactory.selectFrom(menu)
                .where(menu.restaurant.id.eq(UUID.fromString(restaurantId))).fetch();
        var dtoList = entityList.stream().map(entity -> MenuDto.builder()
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .restaurantId(entity.getRestaurant().getId())
                .build()).collect(Collectors.toList());
        
        
        return Optional.of(dtoList);
    }
    public void saveAll(List<Menu> menuList) {
        menuList.forEach(menuEntity -> {
            em.persist(menuEntity);
        });

    }
}
