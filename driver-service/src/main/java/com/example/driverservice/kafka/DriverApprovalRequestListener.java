package com.example.driverservice.kafka;

import com.example.commondata.domain.aggregate.DriverApproval;
import com.example.commondata.domain.aggregate.valueobject.CallId;
import com.example.commondata.domain.aggregate.valueobject.DriverApprovalId;
import com.example.commondata.domain.aggregate.valueobject.DriverId;
import com.example.commondata.domain.aggregate.valueobject.Money;
import com.example.driverservice.config.CallServiceConfigData;
import com.example.kafka.avro.model.DriverApprovalRequestAvroModel;
import com.example.kafka.avro.model.DriverApprovalResponseAvroModel;
import com.example.kafka.avro.model.DriverApprovalStatus;
import com.example.kafkaconsumer.KafkaConsumer;
import com.example.kafkaproducer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Conversions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class DriverApprovalRequestListener implements KafkaConsumer<DriverApprovalRequestAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;


    private final CallServiceConfigData driverConfigData;

    @Value("${kafka-consumer-config.consumer-group-id}")
    private String consumerGroupId;


    private final KafkaProducer<String, DriverApprovalResponseAvroModel> driverApprovalResponseKafkaProducer;

    // utils
    private final Conversions.DecimalConversion decimalConversion = new Conversions.DecimalConversion();


    @EventListener
    public void OnAppStarted(ApplicationStartedEvent event) {
            log.info("on app started!");
            log.info("consumer group id: {} topic: {} ", consumerGroupId,  driverConfigData.getDriverApprovalRequestTopicName());
        kafkaListenerEndpointRegistry.getListenerContainer(consumerGroupId).start();
    }


    @Override
    @KafkaListener(id = "${kafka-consumer-config.consumer-group-id}",
                topics = "${call-service.driver-approval-request-topic-name}")
    public void receive(List<DriverApprovalRequestAvroModel> messages, List<String> keys, List<Integer> partitions, List<Long> offsets) {
            messages.forEach(avroModel -> {
            log.info("driver approval request receive called.");
            //TODO save into DB
            var dto = DriverApproval.builder()
                    .id(new DriverApprovalId(UUID.fromString(avroModel.getId().toString())))
                    .driverId(new DriverId(UUID.fromString(avroModel.getDriverId().toString())))
                    .callId(new CallId(UUID.fromString(avroModel.getCallId().toString())))
                    .price(new Money(decimalConversion.fromBytes(avroModel.getPrice(),
                            avroModel.getSchema().getField("price").schema(),
                            avroModel.getSchema().getField("price").schema().getLogicalType())))
                    .driverApprovalStatus(avroModel.getDriverApprovalStatus())
                    .build();

            // validate,
            // generate paymentSuccess, paymentFail event like CallPaidEvent
            var now = ZonedDateTime.now(ZoneId.of("UTC"));

            var response = DriverApprovalResponseAvroModel.newBuilder()
                    .setId(dto.getId().getValue().toString())
                    .setDriverApprovalStatus(DriverApprovalStatus.APPROVED)
                    .setCallId(dto.getCallId().getValue().toString())
                    .setId(dto.getId().getValue().toString())
                    .setSagaId("")
                    .setDriverId(dto.getDriverId().getValue().toString())
                    .setFailureMessages("")
                    .setCreatedAt(now.toInstant())
                    .build();

            log.info("dto response producer send: {}",  driverConfigData.getDriverApprovalResponseTopicName());
            driverApprovalResponseKafkaProducer.send( driverConfigData.getDriverApprovalResponseTopicName(),
                    avroModel.getCallId().toString(),
                    response);
        });
    }
}