package com.example.eatsorderapplication.repository;


import com.example.commondata.domain.aggregate.valueobject.RestaurantApprovalStatus;
import com.example.eatsorderapplication.config.RepositoryTestConfig;
import com.example.eatsorderdataaccess.entity.OrderApprovalEntity;
import com.example.eatsorderdataaccess.repository.OrderApprovalRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ContextConfiguration(classes = RepositoryTestConfig.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = {"test"})
public class RepositoryTest {

    @Autowired
    private OrderApprovalRepository orderApprovalRepository;

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

}
