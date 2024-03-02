package com.example.driver.stream;
import com.example.kafka.admin.client.KafkaAdminClient;
import com.example.kafka.config.data.KafkaConfigData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KafkaStreamsInitializer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaStreamsInitializer.class);

    private final KafkaConfigData kafkaConfigData;

    private final KafkaAdminClient kafkaAdminClient;

    public KafkaStreamsInitializer(KafkaConfigData configData, KafkaAdminClient adminClient) {
        this.kafkaConfigData = configData;
        this.kafkaAdminClient = adminClient;
    }

    public void init() {
        kafkaAdminClient.createTopics();
//        kafkaAdminClient.checkSchemaRegistry();
        LOG.info("Topics with name {} is ready for operations!", kafkaConfigData.getTopicNamesToCreate().toArray());
    }
}
