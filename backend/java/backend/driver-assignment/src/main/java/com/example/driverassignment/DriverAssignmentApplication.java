package com.example.driverassignment;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Properties;

@SpringBootApplication
public class DriverAssignmentApplication {

    public static void main(String[] args) {
        SpringApplication.run(DriverAssignmentApplication.class, args);
    }

//    @Bean
//    public KafkaStreams kafkaStreams() {
//        Properties props = new Properties();
//        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "delivery-assignment");
//        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
//        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());
//
//        StreamsBuilder builder = new StreamsBuilder();
//
//        // 주문, 기사, 레스토랑 위치 데이터 스트림 생성
//        KStream<String, String> ordersStream = builder.stream("orders");
//        KStream<String, String> driversStream = builder.stream("drivers");
//        KStream<String, String> restaurantsStream = builder.stream("restaurants");
//
//        // 상태 저장소를 활용하여 필요한 데이터를 수집
//        ordersStream.groupByKey().count().toStream().foreach((key, value) -> {
//            // 특정 조건에 따라 헝가리안 알고리즘 실행
//            if (value >= 5) { // 예: 5개의 주문이 쌓이면 실행
//                runHungarianAlgorithm();
//            }
//        });
//
//        KafkaStreams streams = new KafkaStreams(builder.build(), props);
//        streams.start();
//
//        return streams;
//    }
//
//    // 헝가리안 알고리즘 실행 메서드
//    private static void runHungarianAlgorithm() {
//        // 배달기사, 레스토랑, 주문 데이터를 수집하여 비용 행렬 생성
//        HungarianAlgorithm.apply(costMatrix);
//        // 최적 매칭 결과를 assignments 토픽으로 출력
//    }
//
//    public static int[][] createCostMatrix(List<Driver> drivers, List<Order> orders) {
//        int n = Math.max(drivers.size(), orders.size());
//        int[][] costMatrix = new int[n][n];
//
//        for (int i = 0; i < drivers.size(); i++) {
//            for (int j = 0; j < orders.size(); j++) {
//                int distance = calculateDistance(drivers.get(i), orders.get(j));
//                costMatrix[i][j] = distance;
//            }
//        }
//
//        return costMatrix;
//    }
//
//    public static int calculateDistance(Driver driver, Order order) {
//        // 거리 계산 로직
//        return Math.abs(driver.getLocation().getX() - order.getRestaurantLocation().getX())
//            + Math.abs(driver.getLocation().getY() - order.getRestaurantLocation().getY());
//    }
}
