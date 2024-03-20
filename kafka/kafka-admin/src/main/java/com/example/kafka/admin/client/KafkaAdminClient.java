package com.example.kafka.admin.client;


import com.example.kafka.config.data.KafkaConfigData;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaAdminClient {
        private static final Logger log = LoggerFactory.getLogger(KafkaAdminClient.class);

    private final KafkaConfigData kafkaConfigData;
    private final AdminClient adminClient;

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            List<String> topicList = kafkaConfigData.getTopicNamesToCreate();
            List<NewTopic> newTopicList = topicList.stream().map(topic -> new NewTopic(
                    topic.trim(),
                    kafkaConfigData.getNumOfPartitions(),
                    // ReplicationFactor
                    kafkaConfigData.getReplicationFactor()
            )).toList(); // vs .collect(Collectors.toList())
            adminClient.createTopics(newTopicList);

        } catch (Throwable t) {
            throw new KafkaException("Error on creating topics: ", t);
        }
    }




}
