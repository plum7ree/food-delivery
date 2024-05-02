package com.example.user.data.repository;

import com.example.user.data.dto.MenuDto;
import com.example.user.data.entity.Menu;
import com.example.user.data.entity.QMenu;
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
        List<MenuDto> menuDtos = jpaQueryFactory.select(Projections.fields(
                        MenuDto.class,
                        menu.id,
                        menu.name,
                        menu.description,
                        menu.pictureUrl,
                        menu.price
                )).from(menu)
                .where(menu.restaurant.id.eq(UUID.fromString(restaurantId)))
                .fetch();

        return Optional.of(menuDtos);
    }

    public void saveAll(List<Menu> menuList) {
        menuList.forEach(menuEntity -> {
            em.persist(menuEntity);
        });

    }
}
