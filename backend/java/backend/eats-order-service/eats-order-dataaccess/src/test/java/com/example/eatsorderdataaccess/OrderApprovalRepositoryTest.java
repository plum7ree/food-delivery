package com.example.eatsorderdataaccess;


import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.eatsorderdataaccess.config.TestConfig;
import com.example.eatsorderdataaccess.entity.OrderApprovalEntity;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import com.example.eatsorderdataaccess.repository.OrderApprovalRepository;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.time.ZonedDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ContextConfiguration(classes = TestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class OrderApprovalRepositoryTest {

    @Autowired
    private OrderApprovalRepository orderApprovalRepository;
    @Autowired
    private RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;

    @Test
    public void testSaveOrderApproval() {
        OrderApprovalEntity entity = OrderApprovalEntity.builder()
            .id(UUID.randomUUID())
            .restaurantId(UUID.randomUUID())
            .orderId(UUID.randomUUID())
            .status(RestaurantApprovalStatus.PENDING.name())
            .build();

        OrderApprovalEntity savedEntity = orderApprovalRepository.save(entity);
        assertNotNull(savedEntity);
        assertEquals(entity.getId(), savedEntity.getId());

        OrderApprovalEntity foundEntity = orderApprovalRepository.findById(savedEntity.getId()).orElse(null);
        assertNotNull(foundEntity);
        assertEquals(entity.getStatus(), foundEntity.getStatus());
    }


    @Test
    public void testRestaurantApprovalOutboxMessageEntity() {
        // 엔티티 생성
        UUID id = UUID.randomUUID();
        UUID sagaId = UUID.randomUUID();
        ZonedDateTime now = ZonedDateTime.now();
        String sagaType = "TEST_SAGA";
        String payload = "{\"key\": \"value\"}";
        String orderStatus = "PENDING";
        String outboxStatus = "STARTED";
        String sagaStatus = "STARTED";

        RestaurantApprovalOutboxMessageEntity entity = RestaurantApprovalOutboxMessageEntity.builder()
            .id(id)
            .sagaId(sagaId)
            .createdAt(now)
            .processedAt(now)
            .sagaType(sagaType)
            .payload(payload)
            .orderStatus(orderStatus)
            .outboxStatus(outboxStatus)
            .sagaStatus(sagaStatus)
            .build();

        // 엔티티 저장
        RestaurantApprovalOutboxMessageEntity savedEntity = restaurantApprovalRequestOutboxRepository.upsert(entity);

        // 저장된 엔티티 검증
        assertNotNull(savedEntity);
        assertEquals(id, savedEntity.getId());
        assertEquals(sagaId, savedEntity.getSagaId());
        assertEquals(now, savedEntity.getCreatedAt());
        assertEquals(now, savedEntity.getProcessedAt());
        assertEquals(sagaType, savedEntity.getSagaType());
        assertEquals(payload, savedEntity.getPayload());
        assertEquals(orderStatus, savedEntity.getOrderStatus());
        assertEquals(outboxStatus, savedEntity.getOutboxStatus());
        assertEquals(sagaStatus, savedEntity.getSagaStatus());

        // 저장된 엔티티 조회
        RestaurantApprovalOutboxMessageEntity retrievedEntity = restaurantApprovalRequestOutboxRepository.findById(id).orElse(null);
        assertNotNull(retrievedEntity);
        assertEquals(entity, retrievedEntity);
    }
}
