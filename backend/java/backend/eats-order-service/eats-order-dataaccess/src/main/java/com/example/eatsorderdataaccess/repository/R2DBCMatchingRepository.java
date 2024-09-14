package com.example.eatsorderdataaccess.repository;


import com.example.eatsorderdataaccess.entity.MatchingEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.UUID;

@Repository
public class R2DBCMatchingRepository implements MatchingRepository {

    private final DatabaseClient databaseClient;

    public R2DBCMatchingRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    @Override
    public Flux<MatchingEntity> findByStatusIn(Collection<String> status) {
        String query = "SELECT * FROM matching " +
            "WHERE status IN (:status)";

        return databaseClient.sql(query)
            .bind("status", status)
            .map(row -> MatchingEntity.builder()
                .id(row.get("id", UUID.class))
                .status(row.get("status", String.class))
                .userId(row.get("user_id", UUID.class))
                .addressId(row.get("address_id", UUID.class))
                .driverId(row.get("driver_id", UUID.class))
                .build())
            .all();  // Fetches all matching rows
    }
}
