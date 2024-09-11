package com.example.eatsorderapplication.service.driver;

import com.example.eatsorderdataaccess.entity.MatchingEntity;
import com.example.eatsorderdomain.data.dto.AddressDto;
import com.example.kafka.avro.model.DriverDetails;
import com.example.kafka.avro.model.NotificationAvroModel;
import com.example.kafka.avro.model.NotificationType;
import feign.FeignException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.KafkaException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DriverLocationPublisher {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Resource(name = "commonKafkaProducerConfigs")
    // bean 이 map 인 경우는 @Resource 를 사용해야한다. 안그러면 map of map 리 리턴되어서 이름으로 다시 찾아야함.
    private Map<String, Object> commonProducerConfigs;

    private KafkaProducer<String, NotificationAvroModel> kafkaProducer;

    @Value("${kafka-producer-config.driver-matching-notification-transaction-id}")
    private String kafkaProducerTransactionId;

    @PostConstruct
    public void init() {
        initKafkaProducer();

        // 5초마다 processOrders 메서드를 호출
//        scheduler.scheduleAtFixedRate(this::processOrders, POLLING_INTERVAL, POLLING_INTERVAL, TimeUnit.SECONDS);
    }

    private void initKafkaProducer() {
        Map<String, Object> props = commonProducerConfigs;
        props.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, kafkaProducerTransactionId); // Set a unique ID
        props.put(ProducerConfig.TRANSACTION_TIMEOUT_CONFIG, 10000); // 10초로 설정

        kafkaProducer = new KafkaProducer<>(props);
        kafkaProducer.initTransactions();
    }

    @PreDestroy
    public void cleanup() {
        scheduler.shutdown();
    }

    // 큐에 쌓인 주문 처리
    private void processOrders() {

//        List<AddressDto> userLocations = new ArrayList<>();
//        try {
//            kafkaProducer.beginTransaction(); // 반드시 다른 트랜잭션들 앞에 두어야함.
//
//            matchings.forEach(matching -> {
//                NotificationAvroModel message = NotificationAvroModel.newBuilder()
//                    .setUserId(UUID.fromString(matching.getUserId()))
//                    .setNotificationType(NotificationType.DRIVER_MATCHED)
//                    .setDriverDetails(DriverDetails.newBuilder()
//                        .setDriverId(matching.getDriver().getDriverId())
//                        .setLat(matching.getDriver().getLat())
//                        .setLon(matching.getDriver().getLon())
//                        .build())
//                    .build();
//
//                ProducerRecord<String, NotificationAvroModel> record = new ProducerRecord<>("notification", "", message);
//                kafkaProducer.send(record);
//            });
//            kafkaProducer.flush();
//
//            List<MatchingEntity> matchedList = matchings.stream().map(
//                matching -> {
//                    return MatchingEntity.builder()
//                        .id(UUID.randomUUID())
//                        .userId(UUID.fromString(matching.getUserId()))
//                        .driverId(UUID.fromString(matching.getDriver().getDriverId()))
//                        .status("COMPLETED")
//                        .build();
//                }
//            ).toList();
//            kafkaProducer.commitTransaction();
//            log.info("kafkaProducer.commitTransaction() done");
//            /*  3. kafka 전송 */
//        } catch (FeignException e) {
//            // 타임아웃 또는 다른 FeignException 처리
//            log.error("User service is not available e: {}", e.toString());
//            kafkaProducer.abortTransaction();
//            throw new ResponseStatusException(e.status(), "User service is not available", e);
//        } catch (KafkaException e) {
//            log.error("kafka Exception e: {}", e.toString());
//            kafkaProducer.abortTransaction();
//            kafkaProducer.close();
//            initKafkaProducer();
//            throw new KafkaException(e);
//        } catch (Exception e) {
//            log.error("Runtime Exception e: {}", e.toString());
//            kafkaProducer.abortTransaction();
//            kafkaProducer.close();
//            initKafkaProducer();
//            throw new RuntimeException(e);
//        }
//

    }

}
