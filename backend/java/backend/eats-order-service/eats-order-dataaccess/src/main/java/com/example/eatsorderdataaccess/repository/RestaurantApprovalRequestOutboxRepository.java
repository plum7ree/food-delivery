package com.example.eatsorderdataaccess.repository;


import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantApprovalRequestOutboxRepository extends JpaRepository<RestaurantApprovalOutboxMessageEntity, UUID> {

    @Query(value = "SELECT * FROM restaurant_approval_outbox " +
        "WHERE saga_type = :sagaType " +
        "AND outbox_status = CAST(:outboxStatus AS outbox_status) " +
        "AND saga_status = ANY(CAST(:sagaStatuses AS saga_status[]))",
        nativeQuery = true)
    Optional<List<RestaurantApprovalOutboxMessageEntity>> findBySagaTypeAndOutboxStatusAndSagaStatusIn(
        @Param("sagaType") String sagaType,
        @Param("outboxStatus") String outboxStatus,
        @Param("sagaStatuses") String[] sagaStatuses);


    @Query(value = "SELECT * FROM restaurant_approval_outbox " +
        "WHERE saga_type = :sagaType AND outbox_status = CAST(:status AS outbox_status) " +
        "LIMIT 1",
        nativeQuery = true)
    Optional<RestaurantApprovalOutboxMessageEntity> findBySagaTypeAndOutboxStatus(
        @Param("sagaType") String sagaType,
        @Param("status") String status
    );
    

}
