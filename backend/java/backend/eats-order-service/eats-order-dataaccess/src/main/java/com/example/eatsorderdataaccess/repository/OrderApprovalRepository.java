package com.example.eatsorderdataaccess.repository;

import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.eatsorderdataaccess.entity.OrderApprovalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderApprovalRepository extends JpaRepository<OrderApprovalEntity, UUID> {

    Optional<OrderApprovalEntity> findByOrderIdAndStatus(UUID id, RestaurantApprovalStatus status);

}
