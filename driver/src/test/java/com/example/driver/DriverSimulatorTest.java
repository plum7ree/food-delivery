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
import com.example.route.data.dto.PointDto;
import com.example.route.data.dto.RouteResponseDto;
//import jakarta.ws.rs.core.UriBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.util.UriComponentsBuilder;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

// Seems like we can mock WebClient and KafkaProducer
// without doing e2e test.
@SpringBootTest
// disable eureka client which causes a crash.
@TestPropertySource(properties = "spring.cloud.discovery.enabled=false")
@RequiredArgsConstructor
public class DriverSimulatorTest {

    private WebClient webClient;
    @BeforeEach
    public void setup() {
        webClient = WebClient.create();
    }

//    @Test
//    public void testRouteRequestCorrect() {
//        // WebClient 목(mock) 객체 생성
//        WebClient webClientMock = Mockito.mock(WebClient.class);
//        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
//        WebClient.RequestHeadersSpec requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
//        WebClient.ResponseSpec responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);
//
//        // 목(mock) 객체의 동작 설정
//        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
//        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
//            java.util.function.Function<UriComponentsBuilder, URI> uriFunction = invocation.getArgument(0);
//            URI uri = uriFunction.apply(UriComponentsBuilder.fromPath(""));
//
//            // "/api/route" 요청이 아닐 경우 예외 던지기
//            if (!uri.getPath().equals("/api/route/query")) {
//                throw new AssertionError("Expected '/api/route' request, but got: " + uri.getPath());
//            }
//
//            // 요청 파라미터 추출
//            String query = uri.getQuery();
//            String[] params = query.split("&");
//            double startLat = Double.parseDouble(params[0].split("=")[1]);
//            double startLon = Double.parseDouble(params[1].split("=")[1]);
//            double destLat = Double.parseDouble(params[2].split("=")[1]);
//            double destLon = Double.parseDouble(params[3].split("=")[1]);
//
//            // leftUp, rightDown 좌표 범위 내에 있는지 확인
//            double leftUpLat = 37.5665;
//            double leftUpLon = 126.9780;
//            double rightDownLat = 37.5600;
//            double rightDownLon = 126.9750;
//            assertTrue(startLat >= rightDownLat && startLat <= leftUpLat);
//            assertTrue(startLon >= rightDownLon && startLon <= leftUpLon);
//            assertTrue(destLat >= rightDownLat && destLat <= leftUpLat);
//            assertTrue(destLon >= rightDownLon && destLon <= leftUpLon);
//
//            return requestHeadersSpecMock;
//        });
//
//
//        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
//        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
//        when(responseSpecMock.bodyToMono(eq(RouteResponseDto.class))).thenReturn(Mono.just(new RouteResponseDto()));
//        when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).build()));
//
//
//        // DriverSimulator 인스턴스 생성 및 의존성 주입
//        Driver driver = new Driver(webClientMock);
//        DriverManager driverManager = new DriverManager();
//        driverManager.addDriver(driver);
//
//        // 테스트 실행
//        driverManager.setAreaForDriving(37.5665, 126.9780, 37.5600, 126.9750);
//        var routeResponseList = driverManager.queryRoute();
//
//        // response test
//        assertTrue(routeResponseList.size() == 1);
//    }
//    @Test
//    public void testRouteControllerResponse() {
//        // WebClient 목(mock) 객체 생성
//        WebClient webClientMock = Mockito.mock(WebClient.class);
//        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
//        WebClient.RequestHeadersSpec requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);
//        WebClient.ResponseSpec responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);
//
//
//        // 목(mock) 객체의 동작 설정
//        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
//        when(requestHeadersUriSpecMock.uri(any(java.util.function.Function.class))).thenAnswer(invocation -> {
//            java.util.function.Function<UriComponentsBuilder, URI> uriFunction = invocation.getArgument(0);
//            URI uri = uriFunction.apply(UriComponentsBuilder.fromPath("/api/route"));
//            if ("/api/route".equals(uri.getPath())) {
//                return requestHeadersSpecMock;
//            } else {
//                return Mockito.mock(WebClient.RequestHeadersSpec.class);
//            }
//        });
//        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
//        when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(responseSpecMock);
//        when(responseSpecMock.bodyToMono(eq(RouteResponseDto.class))).thenReturn(Mono.just(new RouteResponseDto()));
//        when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.status(HttpStatus.OK).build()));
//
//
//        // DriverSimulator 인스턴스 생성 및 의존성 주입
//        Driver driver = new Driver(webClientMock);
//        DriverManager driverManager = new DriverManager();
//        driverManager.addDriver(driver);
//
//        // 테스트 실행
//        driverManager.setAreaForDriving(37.5665, 126.9780, 37.5600, 126.9750);
//        var routeResponseList = driverManager.queryRoute();
//
//        // response test
//        //TODO remove when(), mock. we really need to test a response from route microservice
//        assertTrue(routeResponseList.size() == 1);
//        assertEquals(routeResponseList.get(0).getPointList().get(0).getLat(), 1.0);
//    }

        @Test
        public void testLocationController() {

        }

}


class Driver {
    private final WebClient webClient; //how to autowire this in a test environment? -> private final + Constructor

    public Driver(WebClient webClient) {
        this.webClient = webClient;
    }
    private Double currLat;
    private Double currLon;


    public RouteResponseDto startRoute(double leftUpLat, double leftUpLon, double rightDownLat, double rightDownLon) {
        AtomicBoolean requestSuccess = new AtomicBoolean(true);
        int maxCount = 10;
        int index = 0;
//        while (!requestSuccess.get()) {
            var start = selectRandomCoordinate(leftUpLat, leftUpLon, rightDownLat, rightDownLon);
            var dest = selectRandomCoordinate(leftUpLat, leftUpLon, rightDownLat, rightDownLon);
            //                // 광화문 삼거리
            //        var startLat = 37.57524;
            //        var startLon = 126.97711;
            //        // 숭례문거리
            //        var destLat = 37.5606;
            //        var destLon = 126.9757;
            var startLat = start.get(0);
            var startLon = start.get(1);

            var destLat = dest.get(0);
            var destLon = dest.get(1);

            var l2Distance = Math.sqrt(Math.pow(destLat - startLat, 2) + Math.pow(destLon - startLon, 2));

//                if(index++ < maxCount) {
//                    System.out.println("start: " + start + " , dest: " + dest);
//                    requestSuccess.set(false);
//                } else {
//                    requestSuccess.set(true);
//                }
            var routeResponse = webClient.get().uri(uriBuilder -> uriBuilder.path("/api/route/query")
                            .queryParam("startLat", startLat)
                            .queryParam("startLon", startLon)
                            .queryParam("destLat", destLat)
                            .queryParam("destLon", destLon)
                            .build())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                            Mono.error(new RuntimeException("4xx Client Error: " + clientResponse.statusCode())))
                    .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                            Mono.error(new RuntimeException("5xx Server Error: " + clientResponse.statusCode())))
                    .bodyToMono(RouteResponseDto.class)
                    .doOnError(error -> {
                        if (error instanceof WebClientResponseException) {
                            WebClientResponseException ex = (WebClientResponseException) error;
                            System.out.println("HTTP Status Code: " + ex.getRawStatusCode());
                            System.out.println("Response Body: " + ex.getResponseBodyAsString());
                        } else {
                            System.out.println("An error occurred: " + error.getMessage());
                        }
                        System.out.println("Retrying the request...");
                    })
                    .retry(3) // 에러 발생 시 최대 3번 재시도
                    .block();
            return routeResponse;
//            var expected = new RouteResponseDto();
//            expected.setPointList(new ArrayList<>(List.of(new PointDto[]{new PointDto(1.0, 2.0)})));
//            assertTrue(routeResponse.equals(new RouteResponseDto()));
//        }
//        return null;
    }

    public void publishLocation(LocationDto locationDto) {
        webClient.post().uri("api/driver/location")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(locationDto)
                .retrieve();


    }

    private List<Double> selectRandomCoordinate(double leftUpLat, double leftUpLon, double rightDownLat, double rightDownLon) {
        // generate random coord between leftUp coord and rightDown coord.
        Random random = new Random();

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

class DriverManager {


    //TODO change this to generic executor. and autowire from my setting.
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    Executor executor = Executors.newWorkStealingPool(100);
    CompletionService<Void> completionService = new ExecutorCompletionService<Void>(executor);
    private final List<Driver> driverList = new ArrayList<>();
    public void addDriver(Driver driver) {
        driverList.add(driver);
    }
    private double leftUpLat, leftUpLon, rightDownLat, rightDownLon;
    public void setAreaForDriving(double leftUpLat, double leftUpLon, double rightDownLat, double rightDownLon) {
        this.leftUpLat = leftUpLat;
        this.leftUpLon = leftUpLon;
        this.rightDownLat = rightDownLat;
        this.rightDownLon = rightDownLon;
    }
    public List<RouteResponseDto> queryRoute() {
        List<CompletableFuture<RouteResponseDto>> futures = driverList.stream().map(driver ->
            CompletableFuture.supplyAsync(() -> driver.startRoute(leftUpLat, leftUpLon, rightDownLat, rightDownLon),executor))
                .collect(Collectors.toList());
        var routeResponseDtoList = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));
        return routeResponseDtoList.join();
    }
    public void publishLocation() {


    }

}













