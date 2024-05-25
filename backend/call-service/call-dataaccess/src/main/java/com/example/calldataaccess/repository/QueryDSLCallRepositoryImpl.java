//package com.example.calldataaccess.repository;
//
//import com.example.calldataaccess.entity.CallEntity;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import jakarta.persistence.EntityManager;
//import org.springframework.data.jpa.repository.Modifying;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.UUID;
//
//@Repository
//public class QueryDSLCallRepositoryImpl implements QueryDSLCallRepository {
//    private final JPAQueryFactory queryFactory;
//
//    public QueryDSLCallRepositoryImpl(JPAQueryFactory queryFactory) {
//        this.queryFactory = queryFactory;
//    }
//
//    // parameter name: limit, pageSize, count automatically calls setMaxResults(limit)
//    // offset calls setFirstResult(offset)
//    @Override
//    public List<CallEntity> findCallEntitiesByUserId(UUID userID, int limit) {
//        QCallEntity callEntity = QCallEntity.callEntity;
//        return queryFactory.selectFrom(callEntity)
//                .where(callEntity.userId.eq(userID))
//                .orderBy(callEntity.id.desc())
//                .limit(limit)
//                .fetch();
//    }
//
//
//}
