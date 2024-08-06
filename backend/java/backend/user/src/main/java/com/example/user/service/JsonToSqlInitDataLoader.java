package com.example.user.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.sql.Time;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * TODO 이 클래스를 data test generator 로 그냥 옮겨버리자.
 */
@Service
@Configuration
@ConditionalOnProperty(name = "app.init.sql-data", havingValue = "true")
public class JsonToSqlInitDataLoader {

    @Autowired
    private Environment env;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        setupJdbcTemplate();
        try {
            ObjectMapper mapper = new ObjectMapper();

            // insert users
            String jsonString = readJsonFile("users/user_list.json");
            JsonNode rootNode = mapper.readTree(jsonString);
            for (JsonNode accountNode : rootNode) {
                insertAccount(accountNode);
            }

            // insert restaurant and menu
            jsonString = readJsonFile("users/web_crawled_restaurant_data.json");
            rootNode = mapper.readTree(jsonString);

            for (JsonNode restaurantNode : rootNode) {
                insertRestaurant(restaurantNode);
            }

            // count result
            countAndPrintTotalAccounts();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupJdbcTemplate() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(env.getProperty("spring.datasource.driver-class-name"));
        dataSource.setUrl(env.getProperty("spring.datasource.url"));
        dataSource.setUsername(env.getProperty("spring.datasource.username"));
        dataSource.setPassword(env.getProperty("spring.datasource.password"));

        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     * warning: remove 'classpath:' prefix
     *
     * @param classPath ex) users/user_list.json
     * @return
     */
    private String readJsonFile(String classPath) {
        try {
            ClassPathResource resource = new ClassPathResource(classPath);
            byte[] jsonData = Files.readAllBytes(resource.getFile().toPath());
            return new String(jsonData);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON file", e);
        }
    }


    private void insertAccount(JsonNode accountNode) {
        String sql = "INSERT INTO user_schema.account (id, username, password, email, profile_pic_url, role, oauth2provider, oauth2sub) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            UUID.fromString(accountNode.get("id").asText()),
            accountNode.get("username").asText(),
            accountNode.get("password").asText(),
            accountNode.get("email").asText(),
            accountNode.has("profile_pic_url") ? accountNode.get("profile_pic_url").asText() : null,
            accountNode.get("role").asText().isEmpty() ? "USER" : accountNode.get("role").asText(), // 기본값으로 "USER" 설정
            accountNode.has("oauth2provider") ? accountNode.get("oauth2provider").asText() : null,
            accountNode.has("oauth2sub") ? accountNode.get("oauth2sub").asText() : null
        );
    }

    private void insertRestaurant(JsonNode restaurantNode) {
        String sql = "INSERT INTO user_schema.restaurant (id, account_id, name, type, open_time, close_time, picture_url1, picture_url2) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            UUID.fromString(restaurantNode.get("id").asText()),
            UUID.fromString(restaurantNode.get("userId").asText()),
            restaurantNode.get("name").asText(),
            restaurantNode.get("type").asText(),
            Time.valueOf(LocalTime.parse(restaurantNode.get("openTime").asText(), DateTimeFormatter.ISO_LOCAL_TIME)),
            Time.valueOf(LocalTime.parse(restaurantNode.get("closeTime").asText(), DateTimeFormatter.ISO_LOCAL_TIME)),
            restaurantNode.get("pictureUrl1").asText(),
            "" // restaurantNode.get("pictureUrl2").asText()
        );

        for (JsonNode menuNode : restaurantNode.get("menuDtoList")) {
            insertMenu(menuNode, restaurantNode.get("id").asText());
        }
    }

    private void insertMenu(JsonNode menuNode, String restaurantId) {
        String sql = "INSERT INTO user_schema.menu (id, name, description, picture_url, price, currency, restaurant_id, created_at, updated_at) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

        UUID menuId = UUID.randomUUID();
        jdbcTemplate.update(sql,
            menuId,
            menuNode.get("name").asText(),
            menuNode.get("description").asText(),
            menuNode.get("pictureUrl").asText(),
            menuNode.get("price").asLong(),
            "KRW",
            UUID.fromString(restaurantId)
        );

        for (JsonNode optionGroupNode : menuNode.get("optionGroupDtoList")) {
            insertOptionGroup(optionGroupNode, menuId);
        }
    }

    private void insertOptionGroup(JsonNode optionGroupNode, UUID menuId) {
        String sql = "INSERT INTO user_schema.option_group (id, description, max_select_number, is_necessary, menu_id) " +
            "VALUES (?, ?, ?, ?, ?)";

        UUID optionGroupId = UUID.randomUUID();
        jdbcTemplate.update(sql,
            optionGroupId,
            optionGroupNode.get("description").asText(),
            optionGroupNode.get("maxSelectNumber").asInt(),
            optionGroupNode.get("necessary").asBoolean(),
            menuId
        );

        for (JsonNode optionNode : optionGroupNode.get("optionDtoList")) {
            insertOption(optionNode, optionGroupId);
        }
    }

    private void insertOption(JsonNode optionNode, UUID optionGroupId) {
        String sql = "INSERT INTO user_schema.option (id, name, cost, currency, option_group_id) " +
            "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
            UUID.fromString(optionNode.get("id").asText()),
            optionNode.get("name").asText(),
            optionNode.get("cost").asLong(),
            "KRW",
            optionGroupId
        );
    }

    private void countAndPrintTotalAccounts() {
        String sql = "SELECT COUNT(*) FROM user_schema.account";
        Integer totalAccounts = jdbcTemplate.queryForObject(sql, Integer.class);
        System.out.println("Total number of accounts inserted: " + totalAccounts);
    }
}