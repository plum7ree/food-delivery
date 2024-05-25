package eatsorderapplication.config;
import com.example.eatsorderconfigdata.CallServiceConfigData;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.test.EmbeddedKafkaBroker;

import java.util.Map;

@TestConfiguration
public class Configurations {

    @Bean
    public EmbeddedKafkaBroker embeddedKafkaBroker() {
        var broker = new EmbeddedKafkaBroker(1, true, "payment-request-topic", "payment-response-topic");

        broker.brokerProperties(Map.of("kafka.broker.timeout.ms", "30000")); // Increase the timeout to 30 seconds
        return broker;

    }
}