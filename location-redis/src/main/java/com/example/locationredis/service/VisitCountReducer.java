//package com.example.locationredis.service;
//
//
//import com.microservices.demo.kafka.avro.model.LocationAvroModel;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//// update redis after reducing requests
//// 만일 같은 edgeId 의 +1, +1, +1 count 요청이 있을경우 -> 최종 요청은 +3 로. 불필요한 쓰기 최소화
//// 만일 oldEdgeId 에 있을 경우 -1 을 해줘야함.
//public class VisitContAggregator {
//
//    public void aggregate(List<LocationAvroModel> locationAvroModelList) {
//        // aggregate count requests into same edgeID among many driverIds.
//        // groupByConcurrent 는 또 뭐지?
//        locationAvroModelList.stream().collect(Collectors.groupingBy(
//                LocationAvroModel::getEdgeId,
//                Collectors.groupingBy(
//                        LocationAvroModel::getEdgeId,
//                        Collectors.mapping(model -> model, Collectors.toList())
//                )));
//
//
//    }
//
//}
//
//import com.microservices.demo.kafka.avro.model.LocationAvroModel;
//import org.redisson.api.RedissonClient;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class VisitCountAggregator {
//
//    private final RedissonClient redissonClient;
//
//    public VisitCountAggregator(RedissonClient redissonClient) {
//        this.redissonClient = redissonClient;
//    }
//
//    public void aggregate(List<LocationAvroModel> locationAvroModelList) {
//        Map<String, List<LocationAvroModel>> edgeIdModelsMap = locationAvroModelList.stream()
//                .collect(Collectors.groupingBy(
//                        LocationAvroModel::getEdgeId,
//                        Collectors.mapping(model -> model, Collectors.toList())
//                ));
//
//        Map<String, Long> visitCounts = edgeIdModelsMap.entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        entry -> calculateVisitCount(entry.getValue())
//                ));
//
//        // Update Redis with the aggregated visit counts
//        updateRedisVisitCounts(visitCounts);
//    }
//
//    private long calculateVisitCount(List<LocationAvroModel> models) {
//        return models.stream()
//                .map(model -> {
//                    String edgeId = model.getEdgeId();
//                    String oldEdgeId = model.getOldEdgeId();
//                    if (edgeId.equals(oldEdgeId)) {
//                        return 0L; // No change, ignore
//                    } else if (edgeId.equals(oldEdgeId)) {
//                        return -1L; // Decrement count
//                    } else {
//                        return 1L; // Increment count
//                    }
//                })
//                .mapToLong(Long::longValue)
//                .sum();
//    }
//
//    private void updateRedisVisitCounts(Map<String, Long> visitCounts) {
//        // 예: Redis Hash에 방문 횟수 저장
//        redissonClient.getMap("visit-counts").putAll(visitCounts);
//    }
//}
//
//
//import com.microservices.demo.kafka.avro.model.LocationAvroModel;
//import org.redisson.api.RedissonClient;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//public class VisitCountAggregator {
//
//    private final RedissonClient redissonClient;
//
//    public VisitCountAggregator(RedissonClient redissonClient) {
//        this.redissonClient = redissonClient;
//    }
//
//    public void aggregate(List<LocationAvroModel> locationAvroModelList) {
//        Map<String, Map<String, Long>> driverIdCounts = locationAvroModelList.stream()
//                .collect(Collectors.groupingBy(
//                        LocationAvroModel::getDriverId,
//                        Collectors.toMap(
//                                model -> model.getEdgeId(),
//                                model -> 1L,
//                                Long::sum,
//                                Collectors.toMap(
//                                        LocationAvroModel::getOldEdgeId,
//                                        model -> -1L,
//                                        Long::sum,
//                                        () -> new HashMap<>()
//                                )
//                        )
//                ));
//
//        Map<String, Long> visitCounts = driverIdCounts.values().stream()
//                .flatMap(driverCounts -> driverCounts.entrySet().stream())
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        Long::sum
//                ));
//
//        // Update Redis with the aggregated visit counts
//        updateRedisVisitCounts(visitCounts);
//    }
//
//    private void updateRedisVisitCounts(Map<String, Long> visitCounts) {
//        // 예: Redis Hash에 방문 횟수 저장
//        redissonClient.getMap("visit-counts").putAll(visitCounts);
//    }
//}