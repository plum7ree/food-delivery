//import org.apache.kafka.clients.consumer.ConsumerConfig;
//import org.apache.kafka.clients.consumer.ConsumerRecords;
//import org.apache.kafka.clients.consumer.KafkaConsumer;
//import org.apache.kafka.clients.consumer.OffsetAndMetadata;
//import org.apache.kafka.common.TopicPartition;
//import org.apache.kafka.common.serialization.StringDeserializer;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import java.time.Duration;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Properties;
//
//@Component
//@Slf4j
//public class CouponIssueRequestKafkaListener {
//
//    private KafkaConsumer<String, CouponIssueRequestAvroModel> consumer;
//
//    private final CouponRepository couponRepository;
//    private final CouponIssueRepository couponIssueRepository;
//
//    @Value("${kafka.bootstrap-servers}")
//    private String bootstrapServers;
//
//    @Value("${kafka.consumer.group-id}")
//    private String groupId;
//
//    @Value("${topic-names.coupon-issue-request-topic-name}")
//    private String topicName;
//
//    public CouponIssueRequestKafkaListener(CouponRepository couponRepository, CouponIssueRepository couponIssueRepository) {
//        this.couponRepository = couponRepository;
//        this.couponIssueRepository = couponIssueRepository;
//    }
//
//    @PostConstruct
//    public void init() {
//        Properties props = new Properties();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, CustomKafkaAvroDeserializer.class.getName());
//        props.put("schema.registry.url", "mock://not-used"); // If using MockSchemaRegistryClient
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//
//        consumer = new KafkaConsumer<>(props);
//        consumer.subscribe(Collections.singletonList(topicName));
//
//        new Thread(this::pollMessages).start();
//    }
//
//    @PreDestroy
//    public void cleanup() {
//        if (consumer != null) {
//            consumer.close();
//        }
//    }
//
//    private void pollMessages() {
//        try {
//            while (true) {
//                ConsumerRecords<String, CouponIssueRequestAvroModel> records = consumer.poll(Duration.ofMillis(100));
//                records.forEach(record -> {
//                    try {
//                        processRecord(record.value());
//                        consumer.commitSync(Collections.singletonMap(
//                            new TopicPartition(record.topic(), record.partition()),
//                            new OffsetAndMetadata(record.offset() + 1)
//                        ));
//                    } catch (Exception e) {
//                        log.error("Error processing record: {}, exception: {}", record.value(), e.getMessage());
//                    }
//                });
//            }
//        } catch (Exception e) {
//            log.error("Error in Kafka polling loop: {}", e.getMessage());
//        } finally {
//            consumer.close();
//        }
//    }
//
//    private void processRecord(CouponIssueRequestAvroModel message) {
//        log.info("coupon issue request topic received");
//        var couponId = message.getCouponId();
//        var issueId = message.getIssueId();
//        CommitChain commitChain = new CommitChain();
//        commitChain
//            .addCommand(new CommitCouponCommand(couponRepository, couponId))
//            .addCommand(new CommitCouponIssueCommand(couponIssueRepository, issueId));
//
//        try {
//            commitChain.execute();
//        } catch (Exception e) {
//            log.error("Error processing message: {}, exception: {}", message, e.getMessage());
//            // Handle rollback if needed
//        }
//    }
//
//    interface DatabaseCommand {
//        void execute() throws Exception;
//    }
//
//    @RequiredArgsConstructor
//    class CommitCouponCommand implements DatabaseCommand {
//
//        private final CouponRepository couponRepository;
//        private final Long couponId;
//
//        @Override
//        public void execute() throws Exception {
//            var coupon = couponRepository.findById(couponId)
//                .orElseThrow(() -> new Exception("Coupon not found"));
//            // 비즈니스 로직 수행
//        }
//    }
//
//    @RequiredArgsConstructor
//    class CommitCouponIssueCommand implements DatabaseCommand {
//
//        private final CouponIssueRepository couponIssueRepository;
//        private final Long issueId;
//
//        @Override
//        public void execute() throws Exception {
//            var couponIssue = couponIssueRepository.findById(issueId)
//                .orElseThrow(() -> new Exception("Coupon Issue not found"));
//            // 비즈니스 로직 수행
//        }
//    }
//
//    class CommitChain {
//
//        private final List<DatabaseCommand> commandList = new ArrayList<>();
//
//        public CommitChain addCommand(DatabaseCommand command) {
//            commandList.add(command);
//            return this;
//        }
//
//        @Transactional
//        public void execute() throws Exception {
//            for (DatabaseCommand command : commandList) {
//                command.execute();
//            }
//        }
//    }
//}
