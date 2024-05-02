package com.example.user.data.repository;

import com.example.user.data.dto.OptionDto;
import com.example.user.data.entity.Option;
import com.example.user.data.entity.QOption;
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

//public interface OptionRepository extends JpaRepository<Option, UUID> {
//}

@Repository
@Transactional
@Slf4j
public class OptionRepository {
    @Autowired
    private final EntityManager em;

    @Autowired
    private final JPAQueryFactory jpaQueryFactory;

    public OptionRepository(EntityManager em, JPAQueryFactory jpaQueryFactory) {
        this.em = em;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    public Optional<OptionDto> findById(UUID id) {
        QOption option = QOption.option;
        return Optional.ofNullable(jpaQueryFactory.select(Projections.fields(
                        OptionDto.class,
                        option.name,
                        option.cost
                ))
                .from(option)
                .where(option.id.eq(id))
                .fetchOne());
    }

    public Optional<List<OptionDto>> findByOptionGroupId(UUID id) {
        QOption option = QOption.option;
        return Optional.ofNullable(jpaQueryFactory.select(Projections.fields(
                        OptionDto.class,
                        option.id,
                        option.name,
                        option.cost
                ))
                .from(option)
                .where(option.optionGroup.id.eq(id))
                .fetch());
    }

    public void saveAll(List<Option> optionList) {
        optionList.forEach(option -> em.persist(option));
    }

    ;


}