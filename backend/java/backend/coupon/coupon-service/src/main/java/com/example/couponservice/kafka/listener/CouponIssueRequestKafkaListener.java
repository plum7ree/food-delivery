package com.example.couponservice.kafka.listener;

import com.example.couponservice.repository.CouponIssueRepository;
import com.example.couponservice.repository.CouponRepository;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import com.example.kafkaconsumer.GeneralKafkaConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssueRequestKafkaListener implements GeneralKafkaConsumer<CouponIssueRequestAvroModel> {

    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    //    private final CallAndPaymentSaga callAndPaymentSaga;
    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Value("${kafka-consumer-group-id.payment-consumer-group-id}")
    private String consumerGroupId;

    @EventListener
    public void OnAppStarted(ApplicationStartedEvent event) {
        log.info("on app started!");
        log.info("consumer group id: {}", consumerGroupId);
        kafkaListenerEndpointRegistry.getListenerContainer(consumerGroupId).start();
    }

    // auto.offset
    @Override
    @KafkaListener(id = "${kafka-consumer-group-id.coupon-issue-consumer-group-id}",
        topics = "${topic-names.coupon-issue-request-topic-name}")
    public void receive(@Payload List<CouponIssueRequestAvroModel> messages,
                        @Header(KafkaHeaders.RECEIVED_KEY) List<String> keys,
                        @Header(KafkaHeaders.RECEIVED_PARTITION) List<Integer> partitions,
                        @Header(KafkaHeaders.OFFSET) List<Long> offsets,
                        Acknowledgment acknowledgment,
                        Consumer<?, ?> consumer) {

        messages.forEach(message -> {
            log.info("payment response topic received");
            var couponId = message.getCouponId();
            var issueId = message.getIssueId();
            CommitChain commitChain = new CommitChain();
            commitChain
                .addCommand(new CommitCouponCommand(couponRepository, couponId))
                .addCommand(new CommitCouponIssueCommand(couponIssueRepository, issueId));

            try {
                commitChain.execute();
                acknowledgment.acknowledge();  // 성공적으로 처리된 경우에만 오프셋을 커밋
            } catch (Exception e) {
                log.error("Error processing message: {}, exception: {}", message, e.getMessage());
                // offset rollback 을 위해 acknowledgment를 호출하지 않음
            }


        });

    }


    interface DatabaseCommand {
        void execute() throws Exception;
    }

    @RequiredArgsConstructor
    class CommitCouponCommand implements DatabaseCommand {

        private final CouponRepository couponRepository;
        private final Long couponId;

        @Override
        public void execute() throws Exception {
            var coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new Exception("Coupon not found"));
            // 비즈니스 로직 수행
        }
    }

    @RequiredArgsConstructor
    class CommitCouponIssueCommand implements DatabaseCommand {

        private final CouponIssueRepository couponIssueRepository;
        private final Long issueId;

        @Override
        public void execute() throws Exception {
            var couponIssue = couponIssueRepository.findById(issueId)
                .orElseThrow(() -> new Exception("Coupon Issue not found"));

        }
    }

    class CommitChain {

        private final List<DatabaseCommand> commandList = new ArrayList<>();

        public CommitChain addCommand(DatabaseCommand command) {
            commandList.add(command);
            return this;
        }

        @Transactional
        public void execute() throws Exception {
            for (DatabaseCommand command : commandList) {
                command.execute();
            }
        }
    }
}
