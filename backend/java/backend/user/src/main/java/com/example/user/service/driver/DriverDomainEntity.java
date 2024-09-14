package com.example.user.service.driver;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Random;
import java.util.UUID;

@Data
@Builder
public class DriverDomainEntity {
    UUID id;
    Double lon;
    Double lat;
    Double speedInKH;

    public static List<Double> selectRandomCoordinate(long seedId, double leftUpLat, double leftUpLon, double rightDownLat, double rightDownLon) {
        // generate random coord between leftUp coord and rightDown coord.
        Random random = new Random(seedId);

        // 좌상단과 우하단 좌표 사이의 위도 범위
        double minLat = Math.min(leftUpLat, rightDownLat);
        double maxLat = Math.max(leftUpLat, rightDownLat);

        // 좌상단과 우하단 좌표 사이의 경도 범위
        double minLon = Math.min(leftUpLon, rightDownLon);
        double maxLon = Math.max(leftUpLon, rightDownLon);

        // 위도와 경도의 범위 내에서 임의의 좌표 생성
        double randomLat = minLat + (maxLat - minLat) * random.nextDouble();
        double randomLon = minLon + (maxLon - minLon) * random.nextDouble();
        return List.of(randomLat, randomLon);
    }
}
