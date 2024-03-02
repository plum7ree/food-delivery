package com.example.kafka.admin.client;

import com.example.kafka.config.data.KafkaConfigData;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.CreateTopicsResult;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.KafkaException;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class KafkaAdminClient {
    private final KafkaConfigData kafkaConfigData;
    private final AdminClient adminClient;
    private final RetryTemplate retryTemplate;

    public void createTopics() {
        CreateTopicsResult createTopicsResult;
        try {
            createTopicsResult = retryTemplate.execute(this::createTopicsFromListCb);

        } catch (Throwable t) {
            throw new KafkaException("Error on creating topics. might reach max number of retry", t);
        }
    }

    public CreateTopicsResult createTopicsFromListCb(RetryContext retryContext) {
        List<String> topicList = kafkaConfigData.getTopicNamesToCreate();
        List<NewTopic> newTopicList = topicList.stream().map(topic -> new NewTopic(
                topic.trim(),
                kafkaConfigData.getNumOfPartitions(),
                // ReplicationFactor
                kafkaConfigData.getReplicationFactor()
        )).toList(); // vs .collect(Collectors.toList())
        return adminClient.createTopics(newTopicList);
    }

}
