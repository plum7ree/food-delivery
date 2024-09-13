package com.example.couponservice.kafka.listener;

import com.example.couponservice.entity.CouponIssue;
import com.example.couponservice.repository.CouponIssueRepository;
import com.example.couponservice.repository.CouponRepository;
import com.example.kafka.avro.model.CouponIssueRequestAvroModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import java.time.Duration;
import java.util.*;

@Component
@Slf4j
public class CouponIssueRequestKafkaConsumer {

    private KafkaConsumer<String, CouponIssueRequestAvroModel> consumer;

    private final CouponRepository couponRepository;
    private final CouponIssueRepository couponIssueRepository;


    @Value("${kafka-consumer-group-id.coupon-issue-request-consumer-group-id}")
    private String groupId;

    @Value("${topic-names.coupon-issue-request-topic-name}")
    private String topicName;

    @Resource(name = "commonKafkaConsumerConfigs")
    // bean 이 map 인 경우는 @Resource 를 사용해야한다. 안그러면 map of map 리 리턴되어서 이름으로 다시 찾아야함.
    private Map<String, Object> commonConsumerConfigs;

    @Autowired
    public CouponIssueRequestKafkaConsumer(
        CouponRepository couponRepository,
        CouponIssueRepository couponIssueRepository,
        Map<String, Object> commonConsumerConfigs) {
        this.couponRepository = couponRepository;
        this.couponIssueRepository = couponIssueRepository;
        this.commonConsumerConfigs = commonConsumerConfigs;
    }

    @PostConstruct
    public void init() {
        Map<String, Object> props = commonConsumerConfigs;
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topicName));

        new Thread(this::pollMessages).start();
    }

    @PreDestroy
    public void cleanup() {
        if (consumer != null) {
            consumer.close();
        }
    }

    private void pollMessages() {
        try {
            while (true) {
                ConsumerRecords<String, CouponIssueRequestAvroModel> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(record -> {
                    if (processRecord(record.value())) {
                        consumer.commitSync(Collections.singletonMap(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset() + 1)
                        ));
                    } else {
                        consumer.commitSync(Collections.singletonMap(
                            new TopicPartition(record.topic(), record.partition()),
                            new OffsetAndMetadata(record.offset())
                        ));
                    }
                });
            }
        } catch (Exception e) {
            log.error("Error in Kafka polling loop: {}", e.getMessage());
        } finally {
            consumer.close();
        }
    }

    private boolean processRecord(CouponIssueRequestAvroModel message) {
        log.info("coupon issue request topic received");
        var couponId = message.getCouponId();
        var issueId = message.getIssueId();
        CommitChain commitChain = new CommitChain();
        commitChain
            .addCommand(new CommitCouponCommand(couponRepository, couponId))
            .addCommand(new CommitCouponIssueCommand(couponIssueRepository, issueId, couponId));

        try {
            commitChain.execute();
        } catch (Exception e) {
            log.error("Error processing message: {}, exception: {}", message, e.getMessage());
            return false;
        }
        return true;
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
                .orElseThrow(() -> new Exception(String.format("Coupon %d not found", couponId)));
            // 비즈니스 로직 수행
            var issuedCount = coupon.getIssuedQuantity();
            coupon.setIssuedQuantity(issuedCount + 1);
            couponRepository.save(coupon);
        }
    }

    @RequiredArgsConstructor
    class CommitCouponIssueCommand implements DatabaseCommand {

        private final CouponIssueRepository couponIssueRepository;
        private final Long issueId;
        private final Long couponId;

        @Override
        public void execute() throws Exception {
            CouponIssue couponIssue = CouponIssue.builder()
                .id(issueId)
                .couponId(couponId)
                .build();


            // 비즈니스 로직 수행
            couponIssueRepository.save(couponIssue);
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
