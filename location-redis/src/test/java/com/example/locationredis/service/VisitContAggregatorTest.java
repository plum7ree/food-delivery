package com.example.locationredis.service;

import com.microservices.demo.kafka.avro.model.LocationAvroModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class VisitContAggregatorTest {
        @Data
        @AllArgsConstructor
        class Model {
            int driverId;
            int currEdgeId;
            int oldEdgeId;
        }



    @Test
    public void testBasicAggregate() {
//        // 방법 1: Arrays.asList() 사용
//        List<Integer> list1 = Arrays.asList(1, 2, 3);
//        System.out.println(list1); // [1, 2, 3]
//
//        // 방법 2: ArrayList 생성자 사용
//        List<Integer> list2 = new ArrayList<>(Arrays.asList(1, 2, 3));
//        System.out.println(list2); // [1, 2, 3]
//
//        // 방법 3: 직접 ArrayList에 요소 추가
//        List<Integer> list3 = new ArrayList<>();
//        list3.add(1);
//        list3.add(2);
//        list3.add(3);
//        System.out.println(list3); // [1, 2, 3]


        List<Model> list = Arrays.asList(
                new Model(1,100, 99),
                new Model(1,101, 100),
                new Model(1, 102, 101),
                new Model(2,103,102)
        );
        var grouped = list.stream().collect(
        Collectors.groupingBy(
                Model::getDriverId,
                Collectors.mapping(model -> model, Collectors.toList())
        ));
        System.out.println(grouped);
        //  {   1=[Model(id=1, incr=1), Model(id=1, incr=-1)],
        //      2=[Model(id=2, incr=1), Model(id=2, incr=-2)],
        //      3=[Model(id=3, incr=1), Model(id=3, incr=-3)]}

        // 1. grouping By result -> entry
        // 결과 : { currEdgeId, [{ driverId, oldEdgeId}] }
        Map<Integer, List<SimpleEntry<Integer, Integer>>> visitCounts = list.stream()
                .collect(Collectors.groupingBy(
                        Model::getCurrEdgeId,
                        Collectors.mapping(
                                model -> new AbstractMap.SimpleEntry<>(model.getDriverId(), model.getOldEdgeId()),
                                Collectors.toList()
                        )
                ));


        // 2. grouping by result -> counting()
         Map<Integer, Long> visitCounts = list.stream()
           .collect(Collectors.groupingBy(
                   Model::getCurrEdgeId,
                   Collectors.counting()
           ))
           .entrySet().stream()
           .collect(Collectors.toMap(
                   Map.Entry::getKey,
                   entry -> entry.getValue() - list.stream()
                           .filter(model -> model.getOldEdgeId() == entry.getKey())
                           .count()
           ));


//        grouped.entrySet().stream()
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        entry -> calculateVisitCount(entry.getValue())
//                ));

//        Map<String, Map<String, Long>> driverIdCounts = list.stream()
//                .collect(Collectors.groupingBy(
//                        Model::getDriverId,
//
//                ));
//
//        Map<String, Long> visitCounts = driverIdCounts.values().stream()
//                .flatMap(driverCounts -> driverCounts.entrySet().stream())
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        Long::sum
//                ));

    }

}
