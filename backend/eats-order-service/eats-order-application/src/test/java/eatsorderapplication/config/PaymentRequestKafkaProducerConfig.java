//package eatsorderapplication.config;
//
//import com.example.eatsorderapplication.service.publisher.kafka.PaymentRequestKafkaProducer;
//import com.example.eatsorderconfigdata.CallServiceConfigData;
//import com.example.kafka.avro.model.RequestAvroModel;
//import com.example.kafka.avro.model.ResponseAvroModel;
//import com.example.kafkaproducer.KafkaProducer;
//import com.example.paymentservice.service.listener.kafka.PaymentRequestKafkaListener;
//import org.apache.kafka.clients.producer.ProducerConfig;
//import org.springframework.boot.test.context.TestConfiguration;
//import org.springframework.context.annotation.Bean;
//import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
//
//import java.util.HashMap;
//import java.util.Map;
//
//
//@TestConfiguration
//public class PaymentRequestKafkaProducerConfig {
//
//
//    @Bean
//    public KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry() {
//        return new KafkaListenerEndpointRegistry();
//    }
//
//    @Bean
//    public KafkaProducer<String, ResponseAvroModel> paymentReponseKafkaProducer(CallServiceConfigData callServiceConfigData) {
//        Map<String, Object> producerConfig = new HashMap<>();
//        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, callServiceConfigData.getKafkaBootstrapServers());
//        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//        return new KafkaProducer<>(producerConfig);
//    }
//
//    @Bean
//    public KafkaProducer<String, RequestAvroModel> driverApprovalRequestKafkaProducer(CallServiceConfigData callServiceConfigData) {
//        Map<String, Object> producerConfig = new HashMap<>();
//        producerConfig.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, callServiceConfigData.getKafkaBootstrapServers());
//        producerConfig.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
//        producerConfig.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
//
//        return new KafkaProducer<>(producerConfig);
//    }
//
//    @Bean
//    public CallServiceConfigData callServiceConfigData() {
//        return new CallServiceConfigData();
//    }
//
//    @Bean
//    public PaymentRequestKafkaListener paymentRequestKafkaListener(KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry,
//                                                                   KafkaProducer<String, ResponseAvroModel> paymentReponseKafkaProducer,
//                                                                   CallServiceConfigData callServiceConfigData) {
//        return new PaymentRequestKafkaListener(kafkaListenerEndpointRegistry, paymentReponseKafkaProducer, callServiceConfigData);
//    }
//}