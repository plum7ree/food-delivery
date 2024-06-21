
package com.example.couponservice.kafka.listener;

import com.example.couponservice.repository.CouponIssueRepository;
import com.example.couponservice.repository.CouponRepository;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import org.apache.kafka.clients.consumer.Consumer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MockTest {

    @Mock
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @Mock
    private CouponRepository couponRepository;

    @Mock
    private CouponIssueRepository couponIssueRepository;

    @InjectMocks
    private CouponIssueRequestKafkaListener listener;

    @Mock
    private Acknowledgment acknowledgment;

    @Mock
    private Consumer<?, ?> consumer;

    @Captor
    private ArgumentCaptor<CouponIssueRequestAvroModel> messageCaptor;

    @BeforeEach
    void setUp() {
        listener = new CouponIssueRequestKafkaListener(kafkaListenerEndpointRegistry, couponRepository, couponIssueRepository);
    }

    @Test
    void testReceive_WhenExceptionThrown_AckNotAcknowledged() throws Exception {
        // Given
        var message = new CouponIssueRequestAvroModel();
        message.setCouponId(1L);
        message.setIssueId(1L);

        List<CouponIssueRequestAvroModel> messages = List.of(message);
        List<String> keys = List.of("key1");
        List<Integer> partitions = List.of(0);
        List<Long> offsets = List.of(0L);

        doThrow(new RuntimeException("Mocked Exception")).when(couponRepository).findById(1L);

        // When
        try {
            listener.receive(messages, keys, partitions, offsets, acknowledgment, consumer);
        } catch (RuntimeException e) {
            // 예외가 발생했는지 검증
            assertEquals("Mocked Exception", e.getMessage());
        }
        // Then
        verify(acknowledgment, never()).acknowledge();
    }
}
