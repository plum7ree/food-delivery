package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.CallEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;


@Repository
public interface CallRepository extends JpaRepository<CallEntity, UUID>
//        , QueryDSLCallRepository
{

    @Modifying
    @Query(value = "INSERT INTO calls (id, user_id, driver_id, price, call_status, failure_messages) " +
            "VALUES (:id, :userId, :driverId, :price, cast(:callStatus as call_schema.call_status_enum), :failureMessages)",
            nativeQuery = true)
    void saveWithCast(@Param("id") UUID id,
                      @Param("userId") UUID userId,
                      @Param("driverId") UUID driverId,
                      @Param("price") BigDecimal price,
                      @Param("callStatus") String callStatus,
                      @Param("failureMessages") String failureMessages);
//
//    // parameter name: limit, pageSize, count automatically calls setMaxResults(limit)
//    // offset calls setFirstResult(offset)
//    @Query("SELECT c FROM CallEntity c WHERE c.userId = :userID ORDER BY c.createdAt DESC")
//    List<CallEntity> findCallEntitiesByUserId(@Param("userID") UUID userID, int limit);


}
