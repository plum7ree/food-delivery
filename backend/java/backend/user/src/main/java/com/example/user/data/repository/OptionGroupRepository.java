package com.example.user.data.repository;


import com.example.user.data.dto.OptionGroupDto;
import com.example.user.data.entity.OptionGroup;
import com.example.user.data.entity.QOptionGroup;
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

//public interface OptionGroupRepository extends JpaRepository<OptionGroup, UUID>  {
//}

@Repository
@Transactional
@Slf4j
public class OptionGroupRepository {

    @Autowired
    private final EntityManager em;

    @Autowired
    private final JPAQueryFactory jpaQueryFactory;

    public OptionGroupRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
        this.em = em;
        this.jpaQueryFactory = jpaQueryFactory;
    }


    public Optional<OptionGroupDto> findById(UUID id) {
        QOptionGroup optionGroup = QOptionGroup.optionGroup;
        return Optional.ofNullable(jpaQueryFactory.select(Projections.fields(
                        OptionGroupDto.class,
                        optionGroup.description,
                        optionGroup.isNecessary,
                        optionGroup.maxSelectNumber
                ))
                .from(optionGroup)
                .where(optionGroup.id.eq(id))
                .fetchOne());
    }

    public Optional<List<OptionGroupDto>> findByMenuId(UUID id) {
        QOptionGroup optionGroup = QOptionGroup.optionGroup;
        return Optional.ofNullable(jpaQueryFactory.select(Projections.fields(
                        OptionGroupDto.class,
                        optionGroup.id,
                        optionGroup.description,
                        optionGroup.isNecessary,
                        optionGroup.maxSelectNumber
                ))
                .from(optionGroup)
                .where(optionGroup.menu.id.eq(id))
                .fetch());
    }


    public void saveAll(List<OptionGroup> optionGroupList) {
        optionGroupList.forEach(optionGroup -> em.persist(optionGroup));
    }

    ;
}
