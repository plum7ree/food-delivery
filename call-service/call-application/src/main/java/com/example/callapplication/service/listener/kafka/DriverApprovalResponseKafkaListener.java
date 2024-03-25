package com.example.callapplication.service.listener.kafka;

import com.example.callapplication.service.CallAndDriverSaga;
import com.example.calldomain.data.mapper.DataMapper;
import com.example.kafka.avro.model.DriverApprovalResponseAvroModel;
import com.example.kafka.avro.model.DriverApprovalStatus;
import com.example.kafkaconsumer.KafkaConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class DriverApprovalResponseKafkaListener implements KafkaConsumer<DriverApprovalResponseAvroModel> {

    private final CallAndDriverSaga callAndDriverSaga;
    private final DataMapper dataMapper;

    @Override
    @KafkaListener(id = "${kafka-consumer-config.driver-approval-response-consumer-group-id}",
                topics = "${call-service.driver-approval-response-topic-name}")
    public void receive(@Payload List<DriverApprovalResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        messages.forEach(responseAvroModel -> {
            if (DriverApprovalStatus.APPROVED == responseAvroModel.getDriverApprovalStatus()) {
                var event = callAndDriverSaga.process(
                        dataMapper.driverApprovalResponseAvroToDriverResponseDto(responseAvroModel));

            }
        });

    }

}

