package com.example.eatsorderapplication.messaging.config;

import com.example.commondata.domain.events.order.OutboxStatus;
import com.example.eatsorderdataaccess.repository.RestaurantApprovalRequestOutboxRepository;
import com.example.kafka.avro.model.RestaurantEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.FluxMessageChannel;
import org.springframework.messaging.Message;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;
import reactor.kafka.sender.SenderResult;

import java.util.UUID;
import java.util.function.Supplier;

@Configuration
@Slf4j
public class MessagePublisher {

    private final RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository;

    public MessagePublisher(RestaurantApprovalRequestOutboxRepository restaurantApprovalRequestOutboxRepository) {
        this.restaurantApprovalRequestOutboxRepository = restaurantApprovalRequestOutboxRepository;
    }

    // OrderService 의 dispatchAfterCommit 에서 tryNextEmit 되었던 메시지 publish
    @Bean
    @Qualifier("restaurantApprovalSinks")
    public Sinks.Many<Message<RestaurantEvent>> restaurantApprovalSinks() {
        return Sinks.many().unicast().onBackpressureBuffer();
    }

    @Bean
    public Supplier<Flux<Message<RestaurantEvent>>> restaurantApprovalSender(
        @Qualifier("restaurantApprovalSinks") Sinks.Many<Message<RestaurantEvent>> sender) {
        return () -> sender.asFlux()
            .onErrorContinue((err, obj) -> {
                log.error("requestRestaurantApproval: e: {}", err.getMessage() != null ? err.getMessage() : "failed to send eventMessage", err);
            });
    }

    // publish 되었던 메시지 sendResult 처리
    private final Sinks.Many<SenderResult<String>> sendResult = Sinks.many().unicast().onBackpressureBuffer();

    @Bean(name = "requestRestaurantApprovalSendResultChannel")
    public FluxMessageChannel sendResultChannel() {
        return new FluxMessageChannel();
    }

    @ServiceActivator(inputChannel = "requestRestaurantApprovalSendResultChannel")
    public void receiveSendResult(SenderResult<String> results) {
        if (results.exception() != null) {
            log.error("sendEventMessage", results.exception().getMessage() != null
                ? results.exception().getMessage()
                : "receive an exception for event message send.", results.exception());
        }

        sendResult.emitNext(results, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @PostConstruct
    public void handleSendResult() {
        sendResult.asFlux()
            .flatMap(result -> {
                if (result.recordMetadata() != null) {
                    return restaurantApprovalRequestOutboxRepository.updateStatus(
                        UUID.fromString(result.correlationMetadata()),
                        OutboxStatus.SENT);
                } else {
                    return restaurantApprovalRequestOutboxRepository.updateStatus(
                        UUID.fromString(result.correlationMetadata()),
                        OutboxStatus.FAILED);
                }
            })
            .onErrorContinue((err, obj) -> log.error("handleSendResult",
                err.getMessage() != null ? err.getMessage() : "failed to mark the outbox message.", err))
            .subscribeOn(Schedulers.newSingle("handle-send-result-event-message"))
            .subscribe();
    }

}
