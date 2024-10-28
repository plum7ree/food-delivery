package com.example.authserver.data.repository;


import com.example.authserver.data.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    @Query(value = "SELECT * FROM address WHERE user_id IN (:userIds)", nativeQuery = true)
    List<Address> findAllByUserIds(@Param("userIds") List<UUID> userIds);
}