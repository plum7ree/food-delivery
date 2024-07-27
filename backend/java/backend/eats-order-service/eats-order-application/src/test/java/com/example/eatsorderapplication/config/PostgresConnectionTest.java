package com.example.eatsorderapplication.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

//psql -h localhost -p 5433 -U postgres -d postgres // 이건 동작하는데?

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5434/postgres",
    "spring.datasource.username=postgres",
    "spring.datasource.password=admin",
    "spring.datasource.driver-class-name=org.postgresql.Driver",
    "datasource.hikari.connection-timeout=30000",
    "logging.level.org.springframework.jdbc=DEBUG",
    "logging.level.com.zaxxer.hikari=DEBUG"
})
public class PostgresConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testDatabaseConnection() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(1));
            assertFalse(connection.isClosed());
        }
    }

    @Test
    public void testSimpleQuery() {
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
        assertEquals(1, result);
    }

    @Test
    public void testDatabaseMetadata() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            assertEquals("PostgreSQL", databaseProductName);
        }
    }
}