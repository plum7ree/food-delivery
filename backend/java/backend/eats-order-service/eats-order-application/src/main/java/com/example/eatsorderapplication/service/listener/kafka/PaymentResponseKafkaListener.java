package com.example.eatsorderapplication.service.listener.kafka;

//import com.example.eatsorderapplication.service.CallAndPaymentSaga;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdomain.data.dto.ResponseDto;
import com.example.kafka.avro.model.OrderStatus;
import com.example.kafka.avro.model.ResponseAvroModel;
import com.example.kafkaconsumer.GeneralKafkaConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Conversions;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentResponseKafkaListener implements GeneralKafkaConsumer<ResponseAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    private final Conversions.DecimalConversion decimalConversion = new Conversions.DecimalConversion();
    private final EatsOrderServiceConfigData eatsOrderServiceConfigData;
//    private final CallAndPaymentSaga callAndPaymentSaga;

    @Value("${kafka-consumer-group-id.payment-consumer-group-id}")
    private String consumerGroupId;

    @EventListener
    public void OnAppStarted(ApplicationStartedEvent event) {
        log.info("on app started!");
        log.info("consumer group id: {}", consumerGroupId);
        kafkaListenerEndpointRegistry.getListenerContainer(consumerGroupId).start();
    }

    //TODO separate listener container id and consumer group id
    // example) @KafkaListener(id = "${kafka-consumer-config.payment-listener-container-id-2}",
    //            groupId = "${kafka-consumer-config.payment-consumer-group-id}",
    //            topics = "${call-service.payment-response-topic-name}")
    @Override
    @KafkaListener(id = "${kafka-consumer-group-id.payment-consumer-group-id}",
        topics = "${topic-names.payment-response-topic-name}")
    public void receive(@Payload List<ResponseAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets) {

        messages.forEach(responseAvroModel -> {
            log.info("payment response topic received");
            if (OrderStatus.PAYMENT_COMPLETED == responseAvroModel.getOrderStatus()) {
                ResponseDto responseDto = new ResponseDto();
                BeanUtils.copyProperties(responseAvroModel, responseDto);
//                var event = callAndPaymentSaga.process(responseDto);
//                log.info(String.valueOf(event));
//                event.fire();
            }
        });

    }
}
