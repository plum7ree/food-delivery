package com.example.eatsorderapplication.service.scheduler;

import com.example.commondata.domain.aggregate.valueobject.*;
import com.example.eatsorderconfigdata.EatsOrderServiceConfigData;
import com.example.eatsorderdataaccess.entity.RestaurantApprovalOutboxMessageEntity;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.eatsorderdomain.data.domainentity.Order;
import com.example.eatsorderdomain.data.mapper.DtoDataMapper;
import com.example.kafka.avro.model.RequestAvroModel;
import com.example.kafkaproducer.KafkaProducer;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.commondata.domain.aggregate.valueobject.SagaType.EATS_ORDER;

@Slf4j
@Component
public class RestaurantApprovalRequestOutboxScheduler {

    private final RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;
    private final KafkaProducer<String, RequestAvroModel> kafkaProducer;
    private final EatsOrderServiceConfigData eatsOrderServiceConfigData;
    private final ObjectMapper objectMapper;


    public RestaurantApprovalRequestOutboxScheduler(RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository, KafkaProducer<String, RequestAvroModel> kafkaProducer, EatsOrderServiceConfigData eatsOrderServiceConfigData, ObjectMapper objectMapper) {
        this.restaurantApprovalRequestOutboxRepository = restaurantApprovalRequestOutboxRepository;
        this.kafkaProducer = kafkaProducer;
        this.eatsOrderServiceConfigData = eatsOrderServiceConfigData;
        this.objectMapper = objectMapper;
    }

    // WARNING 이거 따로 config 로 옮겨서 ObjectMapper 에 Qualifier 적용하면,
    // 이번에는 컨트롤러 파라미터 Dto 에서 deserialize 에러가 뜬다. 아마 ObjectMapper 를 내가 만든걸 참조해서 그러는듯?
    @PostConstruct
    public void init() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(OrderId.class, new JsonDeserializer<OrderId>() {
            @Override
            public OrderId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (p.getCurrentToken() == JsonToken.START_OBJECT) {
                    JsonNode node = p.getCodec().readTree(p);
                    return new OrderId(UUID.fromString(node.get("value").asText()));
                } else {
                    throw new JsonParseException(p, "Expected object for OrderId");
                }
            }
        });
        module.addDeserializer(CallerId.class, new JsonDeserializer<CallerId>() {
            @Override
            public CallerId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (p.getCurrentToken() == JsonToken.START_OBJECT) {
                    JsonNode node = p.getCodec().readTree(p);
                    return new CallerId(UUID.fromString(node.get("value").asText()));
                } else {
                    throw new JsonParseException(p, "Expected object for OrderId");
                }
            }
        });
        module.addDeserializer(CalleeId.class, new JsonDeserializer<CalleeId>() {
            @Override
            public CalleeId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (p.getCurrentToken() == JsonToken.START_OBJECT) {
                    JsonNode node = p.getCodec().readTree(p);
                    return new CalleeId(UUID.fromString(node.get("value").asText()));
                } else {
                    throw new JsonParseException(p, "Expected object for OrderId");
                }
            }
        });
        module.addDeserializer(SimpleId.class, new JsonDeserializer<SimpleId>() {
            @Override
            public SimpleId deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                if (p.getCurrentToken() == JsonToken.START_OBJECT) {
                    JsonNode node = p.getCodec().readTree(p);
                    return new SimpleId(UUID.fromString(node.get("value").asText()));
                } else {
                    throw new JsonParseException(p, "Expected object for OrderId");
                }
            }
        });
        objectMapper.registerModule(module);
    }

    @Transactional
    @Scheduled(fixedDelayString = "${this-service.outbox-scheduler-fixed-rate-ms}",
        initialDelayString = "${this-service.outbox-scheduler-initial-delay-ms}")
    public void processStartedSaga() {
        // db 에서 OutboxStatus.STARTED 인 데이터를 읽어오기. 사실 FAILED 된 것도 읽어와야할듯?

        Optional<List<RestaurantApprovalOutboxMessageEntity>> outboxEntities = Optional.ofNullable(
            restaurantApprovalRequestOutboxRepository.findBySagaTypeAndOutboxStatusAndSagaStatusIn(EATS_ORDER.name(),
                    OutboxStatus.STARTED.name(),
                    new String[]{SagaStatus.STARTED.name(), SagaStatus.COMPENSATING.name()})
                .orElseGet(() -> null));

        if (outboxEntities.isEmpty()) {
            return;
        }


        // OrderDomainObject 가 RestaurantApprovalRequestEntity 에 저장된 payload 이며,
        // OrderDomainObject 와 RequestAvroModel 는 이론적으로 같아야함.
        // 다만, producer 를 보내는데 RequestAvroModel 를 써야하며,
        // DDD 패턴상 메모리에 상주할때는 OrderDomainObject 를 써야함.
        List<Pair<RequestAvroModel, RestaurantApprovalOutboxMessageEntity>> outboxMessagesWithEntities = outboxEntities.get().stream().map(entity -> {
            try {
                Order orderDomainObject = objectMapper.readValue(entity.getPayload(), Order.class);
                var avroModel = DtoDataMapper.orderToRequestAvro(orderDomainObject, entity.getSagaId());
                return Pair.of(avroModel, entity);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).toList();


        if (!outboxMessagesWithEntities.isEmpty()) {
            outboxMessagesWithEntities.forEach(pair -> {
                RequestAvroModel outboxMessage = pair.getFirst();
                RestaurantApprovalOutboxMessageEntity originalEntity = pair.getSecond();
                kafkaProducer.sendAndRunCallback(eatsOrderServiceConfigData.getRestaurantApprovalRequestTopicName(),
                    "key",
                    outboxMessage,
                    (metadata, exception) -> {
                        log.info("Received successful response from Kafka " +
                                " Topic: {} Partition: {} Offset: {} Timestamp: {}",
                            metadata.topic(),
                            metadata.partition(),
                            metadata.offset(),
                            metadata.timestamp());
                        originalEntity.setOrderStatus(OrderStatus.PENDING.name());
                        originalEntity.setOutboxStatus(OutboxStatus.COMPLETED.name());
                        originalEntity.setSagaStatus(SagaStatus.PROCESSING.name());
                        originalEntity.setProcessedAt(ZonedDateTime.now());
                        originalEntity.setCreatedAt(originalEntity.getCreatedAt());
                        originalEntity.setVersion(originalEntity.getVersion());

                        restaurantApprovalRequestOutboxRepository.save(originalEntity);
                        log.info("OrderPaymentOutboxMessage is updated with outbox status: {}", OutboxStatus.COMPLETED.name());
                    });
            });
        }

    }

    /**
     * 완료 되었으면 사용자에게 알려줘야함.
     */
    @Transactional
    @Scheduled(fixedDelayString = "${this-service.outbox-scheduler-fixed-rate-ms}",
        initialDelayString = "${this-service.outbox-scheduler-initial-delay-ms}")
    public void processSucceededSaga() {
        // db 에서 OutboxStatus.STARTED 인 데이터를 읽어오기. 사실 FAILED 된 것도 읽어와야할듯?

        Optional<List<RestaurantApprovalOutboxMessageEntity>> outboxEntities = Optional.ofNullable(
            restaurantApprovalRequestOutboxRepository.findBySagaTypeAndOutboxStatusAndSagaStatusIn(EATS_ORDER.name(),
                    OutboxStatus.COMPLETED.name(),
                    new String[]{SagaStatus.SUCCEEDED.name()})
                .orElseGet(() -> null));


    }

}