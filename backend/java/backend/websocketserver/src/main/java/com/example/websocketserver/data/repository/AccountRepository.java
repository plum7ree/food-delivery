package com.example.websocketserver.data.repository;

import com.example.websocketserver.data.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AccountRepository extends JpaRepository<Account, UUID> {
    Account findByUsername(String username);

    Optional<Account> findByEmail(String username);

    Optional<Account> findByOauth2Sub(String sub);
}