package com.example.eatsorderapplication.testcontainer;

import com.example.kafka.avro.model.PaymentRequestAvroModel;
import com.example.kafka.avro.model.PaymentStatus;
import com.google.common.collect.ImmutableMap;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;

import java.nio.ByteBuffer;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class KafkaContainerClusterSchemaRegistryTestV2 {

    @Test
    void testKafkaContainerCluster() throws Exception {
        try (KafkaContainerCluster cluster = new KafkaContainerCluster("6.2.0", 3, 2)) {
            cluster.start();
            String bootstrapServers = cluster.getBootstrapServers();
            String schemaRegistryUrl = cluster.getSchemaRegistryUrl(); // Assuming KafkaContainerCluster provides this

            assertThat(cluster.getBrokers()).hasSize(3);

            testKafkaFunctionality(bootstrapServers, schemaRegistryUrl, 3, 2);
        }
    }

    protected void testKafkaFunctionality(String bootstrapServers, String schemaRegistryUrl, int partitions, int rf) throws Exception {
        try (
            AdminClient adminClient = AdminClient.create(
                ImmutableMap.of(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers)
            )
//                KafkaProducer<String, PaymentRequestAvroModel> producer = new KafkaProducer<>(
//                ImmutableMap.of(
//                    ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                    bootstrapServers,
//                    ProducerConfig.CLIENT_ID_CONFIG,
//                    UUID.randomUUID().toString(),
//                    "schema.registry.url",
//                    schemaRegistryUrl
//                ),
//                new StringSerializer(),
//                new KafkaAvroSerializer()
//            );

//                KafkaConsumer<String, PaymentRequestAvroModel> consumer = new KafkaConsumer<>(
//                ImmutableMap.of(
//                    ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
//                    bootstrapServers,
//                    ConsumerConfig.GROUP_ID_CONFIG,
//                    "tc-" + UUID.randomUUID(),
//                    ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
//                    "earliest",
//                    "schema.registry.url",
//                    schemaRegistryUrl
//                ),
//                new StringDeserializer(),
//                new KafkaAvroDeserializer()
//            );
        ) {
            Properties producerProps = new Properties();
            producerProps.put("bootstrap.servers", bootstrapServers);
            producerProps.put("key.serializer", KafkaAvroSerializer.class.getName());
            producerProps.put("value.serializer", KafkaAvroSerializer.class.getName());
            producerProps.put("schema.registry.url", schemaRegistryUrl);

            Producer<String, PaymentRequestAvroModel> producer = new KafkaProducer<>(producerProps);

            Properties consumerProps = new Properties();
            consumerProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
            consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
            consumerProps.put("schema.registry.url", schemaRegistryUrl);
            consumerProps.put("specific.avro.reader", "true");

            Consumer<String, PaymentRequestAvroModel> consumer = new KafkaConsumer<>(consumerProps);


            String topicName = "messages";

            Collection<NewTopic> topics = Collections.singletonList(new NewTopic(topicName, partitions, (short) rf));
            adminClient.createTopics(topics).all().get(30, TimeUnit.SECONDS);

            consumer.subscribe(Collections.singletonList(topicName));

            PaymentRequestAvroModel message = new PaymentRequestAvroModel(
                UUID.randomUUID().toString(), // id
                UUID.randomUUID().toString(), // sagaId
                UUID.randomUUID().toString(), // userId
                UUID.randomUUID().toString(), // callId
                ByteBuffer.wrap(new java.math.BigDecimal("123.45").unscaledValue().toByteArray()), // price
                Instant.now(), // createdAt
                PaymentStatus.COMPLETED // paymentStatus
            );
            producer.send(new ProducerRecord<>(topicName, message)).get();

            Unreliables.retryUntilTrue(
                10,
                TimeUnit.SECONDS,
                () -> {
                    ConsumerRecords<String, PaymentRequestAvroModel> records = consumer.poll(Duration.ofMillis(100));

                    if (records.isEmpty()) {
                        return false;
                    }

                    assertThat(records)
                        .hasSize(1)
                        .extracting(ConsumerRecord::topic, ConsumerRecord::key, ConsumerRecord::value)
                        .containsExactly(tuple(topicName, message));

                    return true;
                }
            );

            consumer.unsubscribe();
        }
    }
}
