package com.example.eatsorderdataaccess.repository;

import com.example.eatsorderdataaccess.entity.MatchingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface MatchingRepository {

    Flux<MatchingEntity> findByStatusIn(Collection<String> status);
}
