package com.example.user.data.repository;


import com.example.user.data.entity.RefreshTokenEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepository {

    private final NamedParameterJdbcTemplate databaseClient;


    public Optional<RefreshTokenEntity> save(RefreshTokenEntity token) {
        String sql = "INSERT INTO refresh_token (email, value, created_at, updated_at) " +
            "VALUES (:email, :value, :createdAt, :updatedAt) " +
            "ON CONFLICT (email) DO UPDATE SET " +
            "value = :value, updated_at = :updatedAt";
        var parameters = new MapSqlParameterSource();
        parameters.addValue("email", token.email());
        parameters.addValue("value", token.value());
        parameters.addValue("createdAt", token.createdAt());
        parameters.addValue("updatedAt", token.updatedAt());
        int how_many_updated = databaseClient.update(sql, parameters);
        return how_many_updated > 0 ? Optional.of(token) : Optional.empty();
    }
}
