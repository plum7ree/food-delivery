package com.example.driver;//package com.example.driver;
//import lombok.AccessLevel;
//import lombok.Data;
//import lombok.Getter;
//
//import java.util.Collection;
//import java.util.Iterator;
//
//import static io.restassured.RestAssured.*;
//import static io.restassured.matcher.RestAssuredMatchers.*;
//import static org.hamcrest.Matchers.*;
//
///**
// * given velocity, each edge in a path will be divided.
// * the velocity might be changed.
// * each edge has angle (ratio)
// * v^2 = dx^2 + dy^2
// * dx = sqrt( (1/(a^2+1)) * v^2 )
// * y = a*x, a = (endY - startY) / (endX - startX)
// * @param <Path>
// */
//@Data
//public class PathDividerIterator<Path extends Collection<Edge>> implements Iterator<Point> {
//
//    Path path;
//    Long velocity;
//
//    @Getter(AccessLevel.NONE)
//    double dx, dy;
//    @Getter(AccessLevel.NONE)
//    double lon, lat;
//    @Getter(AccessLevel.NONE)
//    int currEdgeIdx;
//    @Getter(AccessLevel.NONE)
//    int currEdgeSegmentIdx;
//    @Override
//    public boolean hasNext() {
//        return currEdgeIdx <= path.size() - 1;
//    }
//
//    @Override
//    public Point next() {
//        Edge currEdge = path.at(currEdgeIdx);
//        double a = (currPoint.getEndY() - currPoint.getStartY()) / (currPoint.getEndX() - currPoint.getStartX());
//        dx = Math.sqrt( (1/(Math.pow(a,2) + 1)) * Math.pow(velocity,2));
//        dy = a * dx;
//        lon += dx;
//        lat += dy;
//        // if newLon, newLat exceeds replace with endX, endY
//        // condition1: newLon이 startX와 endX 사이에 있는지 확인
//        Supplier<Boolean> condition1 = () -> (newLon >= Math.min(startX, endX) && newLon <= Math.max(startX, endX));
//
//        // condition2: newLat이 startY와 endY 사이에 있는지 확인
//        Supplier<Boolean> condition2 = () -> (newLat >= Math.min(startY, endY) && newLat <= Math.max(startY, endY));
//
//        if (condition1.get() && condition2.get()) {
//            // newLon, newLat가 Edge의 범위 내에 있을 때의 로직
//        } else {
//            // newLon, newLat가 Edge의 범위를 벗어났을 때의 로직
//            return new Point(endX, endY);
//        }
//
//        // Edge 범위 내에 있으면 새로운 Point 반환
//        return new Point(newLon, newLat);
//    }
//
//}
//
//public class Driver {
//
//    public currPoint;
//
//    private PathDividerIterator pathDivider;
//
//    public Point getCurrPoint() {
//        return currPoint;
//    }
//    public void startDrive(Path path) {
//
//    }
//    public currEdgeId() {
//        return path[currEdgeIdx].id;
//    }
//
//    public void IntervalCb() {
//        while(pathDivider.hasNext()) {
//            point = pathDivider.next();
//
//        }
//
//    }
//
//}
//
//
//public class DriverSimulator {
//
//    public void drive() {
//        jsonResponse = request(addressA, addressB);
//        Path path = Path.convert(jsonResponse.get("path"));
//        Driver driver = new Driver(path);
//        driver.startDrive();
//        addInterval(intervalCb)
//
//    }
//
//    private void intervalCb() {
//        currentLocation
//    }
//}


import com.example.driver.dto.LocationDto;
import com.example.route.data.dto.AddressSearchResponseDto;
import com.example.route.data.dto.AddressSearchRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.Point;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

class Address {
    String address;
}



@RequiredArgsConstructor
class Driver {

        //TODO how to autowire this in a test environment?
    private final WebClient webClient;
    private Double currLat;
    private Double currLon;

    public CompletableFuture<Void> startRoute() {
                // 광화문 삼거리
        var startLat = 37.57524;
        var startLon = 126.97711;
        // 숭례문거리
        var destLat = 37.5606;
        var destLon = 126.9757;
        var future = Mono.just(webClient.get().uri(uriBuilder -> uriBuilder.path("/api/route/query")
                .queryParam("startLat", startLat)
                .queryParam("startLon", startLon)
                .queryParam("destLat", destLat)
                .queryParam("destLon", destLon)
                .build())
                .retrieve();

    }

    public void updateCurrLocation(LocationDto locationDto) {
        webClient.post().uri("api/location/update")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(locationDto)
                .retrieve();


    }

}

class DriverManager {


    //TODO change this to generic executor. and autowire from my setting.
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<Driver> driverList = new ArrayList<>();
    public void addDriver(Driver driver) {
        driverList.add(driver);
    }
    public void queryRoute() {
        final Runnable locationUpdater = () -> driverList.forEach(Driver::updateLocation);
        CompletableFuture<> future
    }
    public void publishLocation() {

    }

}











