package eatsorderapplication;
import com.example.eatsorderapplication.CallApplication;
import com.example.eatsorderconfigdata.CallServiceConfigData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;


import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = CallApplication.class)
@EmbeddedKafka(partitions = 1, ports = {9092})
@TestPropertySource(locations = "classpath:application.yml")
@ActiveProfiles("test")
public class PaymentRequestProducerAndListenerWithEmbeddedKafkaTest {

    @Autowired
    private CallServiceConfigData callServiceConfigData;

//    @Autowired
//    private PaymentRequestKafkaProducer paymentRequestKafkaProducer;

    @Test
    public void testCallServiceConfigDataLoading() {
        assertEquals("payment-request-topic", callServiceConfigData.getPaymentRequestTopicName());
        assertEquals("payment-response-topic", callServiceConfigData.getPaymentResponseTopicName());
        assertEquals("driver-approval-request-topic", callServiceConfigData.getDriverApprovalRequestTopicName());
        assertEquals("driver-approval-response-topic", callServiceConfigData.getDriverApprovalResponseTopicName());
//        assertEquals("restaurant-approval-request-topic", callServiceConfigData.getRestaurantApprovalRequestTopicName());
    }
}