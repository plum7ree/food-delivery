package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.PaymentOutboxMessageEntity;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentOutboxRepository extends JpaRepository<PaymentOutboxMessageEntity, UUID> {

    @Query(value = "SELECT * FROM payment_outbox " +
        "WHERE saga_id = :sagaId " +
        "AND saga_status = ANY(CAST(:sagaStatus AS saga_status))",
        nativeQuery = true)
    Optional<PaymentOutboxMessageEntity> findBySagaIdAndSagaStatus(
        @Param("sagaId") String sagaType,
        @Param("sagaStatus") String sagaStatus);


}