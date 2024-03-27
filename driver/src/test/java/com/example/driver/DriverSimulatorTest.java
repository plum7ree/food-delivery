package com.example.driver;//package com.example.driver;

import com.example.driver.data.dto.LocationDto;
import com.example.route.data.dto.InstructionDto;
import com.example.route.data.dto.PointDto;
import com.example.route.data.dto.RouteResponseDto;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.config.CHProfile;
import com.graphhopper.config.Profile;
import com.graphhopper.util.shapes.GHPoint;
import io.netty.channel.ChannelOption;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


class Constant {
    public static final String gatewayHost = "localhost";
    public static final String gatewayPort = "8072";
    public static final String routeQueryPath = "/route/api/query";
    public static final String locationPublishPath = "/driver/location/api/update";
}

// Seems like we can mock WebClient and KafkaProducer
// without doing e2e test.
//@SpringBootTest
// disable eureka client which causes a crash.
@TestPropertySource(properties = "spring.cloud.discovery.enabled=false")
@RequiredArgsConstructor
public class DriverSimulatorTest {
    private static final Logger log = LoggerFactory.getLogger(DriverSimulatorTest.class);
    final int NUM_DRIVER = 40;
    private GraphHopper graphHopper;
    private WebClient webClient;

    @BeforeEach
    public void setup() {

        webClient = WebClient.builder()
                .baseUrl("http://localhost:8072")
//        .defaultHeaders(headers -> headers.setBasicAuth("username", "password"))
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(Duration.ofSeconds(2))
                        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)))
                .build();

        URL res = getClass().getClassLoader().getResource("static/seoul-non-military.osm.pbf");
        var ghLoc = res.getPath();
        GraphHopper hopper = new GraphHopper();
        hopper.setOSMFile(ghLoc);
        hopper.setGraphHopperLocation("target/routing-graph-cache");
        hopper.setProfiles(new Profile("car").setVehicle("car").setTurnCosts(false));
        hopper.getCHPreparationHandler().setCHProfiles(new CHProfile("car"));
        hopper.importOrLoad();

        graphHopper = hopper;


    }

    @Test
    public void testRouteRequestE2E() {

        // DriverSimulator 인스턴스 생성 및 의존성 주입
        // DriverSimulator 인스턴스 생성 및 의존성 주입
        DriverSimulator driverSimulator = new DriverSimulator(webClient);

        // Use IntStream.range() to create 100 drivers
        IntStream.range(0, NUM_DRIVER).forEach(i -> {
//            Driver driver = new Driver(String.valueOf(i), 0.0, 0.0);
            String driverId = UUID.randomUUID().toString();
            Driver driver = new Driver(driverId, 0.0, 0.0);
            driverSimulator.addDriver(driver);
        });

        // 테스트 실행
        driverSimulator.setAreaForDriving(37.5784, 126.9255, 37.4842, 127.0842);
        var routeResponseList = driverSimulator.run();


        // response test
        assertEquals(1, routeResponseList.size());
    }


    @Test
    public void testRouteRequestCorrectWithWebClientMock() {
        // WebClient 목(mock) 객체 생성
        WebClient webClientMock = Mockito.mock(WebClient.class);
        WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock = Mockito.mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec requestHeadersSpecMock = Mockito.mock(WebClient.RequestHeadersSpec.class);

        WebClient.RequestBodyUriSpec requestBodyUriSpecMock = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec requestBodySpecMock = Mockito.mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);

        // 목(mock)
        // 객체의 동작 설정
        when(webClientMock.get()).thenReturn(requestHeadersUriSpecMock);
        when(webClientMock.post()).thenReturn(requestBodyUriSpecMock);

        when(requestHeadersUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            java.util.function.Function<UriComponentsBuilder, URI> uriFunction = invocation.getArgument(0);
            URI uri = uriFunction.apply(UriComponentsBuilder.fromPath(""));
//            log.info("request arrived. path: " + uri.getPath());

            // "/api/route" 요청
            if (uri.getPath().equals(Constant.routeQueryPath)) {
//                log.info("get /route/api/query");
                //TODO duplicate code in driver/.../RouteController.java
                // 요청 파라미터 추출
                String query = uri.getQuery();
                String[] params = query.split("&");
                double startLat = Double.parseDouble(params[0].split("=")[1]);
                double startLon = Double.parseDouble(params[1].split("=")[1]);
                double destLat = Double.parseDouble(params[2].split("=")[1]);
                double destLon = Double.parseDouble(params[3].split("=")[1]);

                GHPoint startPoint = new GHPoint();
                startPoint.lat = startLat;
                startPoint.lon = startLon;

                GHPoint endPoint = new GHPoint();
                endPoint.lat = destLat;
                endPoint.lon = destLon;

                GHRequest req = new GHRequest(startPoint, endPoint).setProfile("car").setLocale(Locale.US);
                GHResponse res = graphHopper.route(req);

                var bestPath = res.getBest();
                var points = bestPath.getPoints();
                var instructions = bestPath.getInstructions();

                var pointDtoList = StreamSupport.stream(Spliterators.spliteratorUnknownSize(points.iterator(), 0), false)
                        .map(point -> new PointDto(point.getLat(), point.getLon()))
                        .collect(Collectors.toList());

                var instructionDtoList = StreamSupport.stream(Spliterators.spliteratorUnknownSize(instructions.iterator(), 0), false)
                        .map(instruction -> new InstructionDto(instruction.getSign(), instruction.getName(), instruction.getDistance(), instruction.getTime()))
                        .collect(Collectors.toList());
//                    log.info("path : " + pointDtoList);


                var routeResponseDto = new RouteResponseDto(pointDtoList, instructionDtoList);

                // Mono.just()로 ResponseEntity를 래핑하고 requestHeadersSpecMock를 반환
                when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
                when(responseSpecMock.bodyToMono(RouteResponseDto.class)).thenReturn(Mono.just(routeResponseDto));
                when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));
                // onStatus 메서드 체인의 반환 값을 모킹
                WebClient.ResponseSpec onStatusResponseSpecMock = Mockito.mock(WebClient.ResponseSpec.class);
                when(responseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(onStatusResponseSpecMock);
                when(onStatusResponseSpecMock.onStatus(any(Predicate.class), any(Function.class))).thenReturn(onStatusResponseSpecMock);
                when(onStatusResponseSpecMock.bodyToMono(RouteResponseDto.class)).thenReturn(Mono.just(routeResponseDto));

                return requestHeadersSpecMock;
            }
            return requestHeadersSpecMock;
        });


        when(requestBodyUriSpecMock.uri(any(Function.class))).thenAnswer(invocation -> {
            java.util.function.Function<UriComponentsBuilder, URI> uriFunction = invocation.getArgument(0);
            URI uri = uriFunction.apply(UriComponentsBuilder.fromPath(""));
//            log.info("request arrived. path: " + uri.getPath());
            // "/driver/location/api/update" 요청 (POST)
            if (uri.getPath().equals(Constant.locationPublishPath)) {
                when(requestBodyUriSpecMock.uri(anyString())).thenReturn(requestBodySpecMock);
                when(requestBodySpecMock.accept(any())).thenReturn(requestBodySpecMock);
                when(requestBodySpecMock.contentType(any(MediaType.class))).thenReturn(requestBodySpecMock);
                when(requestBodySpecMock.bodyValue(any(LocationDto.class))).thenReturn(requestHeadersSpecMock);
                when(requestBodySpecMock.bodyValue(any(LocationDto.class))).thenAnswer(invocationOnMock -> {
                    LocationDto locationDto = invocationOnMock.getArgument(0);
//                    log.info("Received LocationDto: " + locationDto);
                    return requestHeadersSpecMock;
                });

                when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
                when(responseSpecMock.toBodilessEntity()).thenReturn(Mono.just(ResponseEntity.ok().build()));

                return requestBodySpecMock;
            }

            return requestBodyUriSpecMock;
        });


        // DriverSimulator 인스턴스 생성 및 의존성 주입

        Driver driver1 = new Driver("1", 0.0, 0.0);
        Driver driver2 = new Driver("2", 0.0, 0.0);
        Driver driver3 = new Driver("3", 0.0, 0.0);
        Driver driver4 = new Driver("4", 0.0, 0.0);
        Driver driver5 = new Driver("5", 0.0, 0.0);
        Driver driver6 = new Driver("6", 0.0, 0.0);
        DriverSimulator driverSimulator = new DriverSimulator(webClientMock);
        driverSimulator.addDriver(driver1);
        driverSimulator.addDriver(driver2);
        driverSimulator.addDriver(driver3);
        driverSimulator.addDriver(driver4);
        driverSimulator.addDriver(driver5);
        driverSimulator.addDriver(driver6);

        // 테스트 실행
        driverSimulator.setAreaForDriving(37.5665, 126.9780, 37.5600, 126.9750);
        var routeResponseList = driverSimulator.run();

        // response test
        assertEquals(1, routeResponseList.size());
    }

    /**
     * Prerequiresite:
     * redis docker, kafka docker, configserver, eurekaserver, gatewayserver, route, locationredis
     * 순서대로 실행 세팅 해야함.
     */


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

@Data
@RequiredArgsConstructor
class Driver {
    private String id;
    private Double currLat;
    private Double currLon;

    public Driver(String id, double lat, double lon) {
        this.id = id;
        this.currLat = lat;
        this.currLon = lon;
    }

}

class DriverSimulator {
    private static final Logger log = LoggerFactory.getLogger(DriverSimulator.class);
    private final WebClient webClient; //how to autowire this in a test environment? -> private final + Constructor
    //TODO change this to multi executor
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final List<Driver> driverList = new ArrayList<>();
    Executor executor = Executors.newWorkStealingPool(100);
    CompletionService<Void> completionService = new ExecutorCompletionService<Void>(executor);
    private double leftUpLat, leftUpLon, rightDownLat, rightDownLon;

    public DriverSimulator(WebClient webClient) {
        this.webClient = webClient;
    }

    public void addDriver(Driver driver) {
        driverList.add(driver);
    }

    public void setAreaForDriving(double leftUpLat, double leftUpLon, double rightDownLat, double rightDownLon) {
        this.leftUpLat = leftUpLat;
        this.leftUpLon = leftUpLon;
        this.rightDownLat = rightDownLat;
        this.rightDownLon = rightDownLon;
    }

    public List<RouteResponseDto> run() {
        List<CompletableFuture<RouteResponseDto>> futures = driverList.stream().map(driver ->
                        CompletableFuture.supplyAsync(() -> {
                            var maxCount = 10;
                            List<PointDto> path = null;
                            RouteResponseDto routeResponseDto = null;
                            for (int i = 0; i < maxCount; i++) {

                                // For randomness, we need different input.
                                var driverId = driver.getId();
                                long seedId = UUID.fromString(driverId).getLeastSignificantBits();

                                var start = selectRandomCoordinate(seedId, leftUpLat, leftUpLon, rightDownLat, rightDownLon);
                                var dest = selectRandomCoordinate(seedId * 3, leftUpLat, leftUpLon, rightDownLat, rightDownLon);
                                log.info("driver id: " + driverId + " start point: " + start + " dest point: " + dest);
                                //TODO move this injection outside of the scope.
                                driver.setCurrLat(start.get(0));
                                driver.setCurrLon(start.get(1));

                                // 광화문 삼거리
                                // var startLat = 37.57524;
                                // var startLon = 126.97711;
                                // 숭례문거리
                                // var destLat = 37.5606;
                                // var destLon = 126.9757;

                                var startLat = start.get(0);
                                var startLon = start.get(1);

                                var destLat = dest.get(0);
                                var destLon = dest.get(1);

                                var l2Distance = Math.sqrt(Math.pow(destLat - startLat, 2) + Math.pow(destLon - startLon, 2));


                                routeResponseDto = queryRoute(startLat, startLon, destLat, destLon);

                                path = routeResponseDto.getPointList();
                                if (path == null) {
                                    throw new RuntimeException("path is null");
                                }
                                if (path.size() > 0) {
                                    break;
                                }
                            }

//                            log.info("getPointList: " + path);
                            path.stream().forEach(p -> {
                                var currLat = driver.getCurrLat();
                                var currLon = driver.getCurrLon();
                                var targetLat = p.getLat();
                                var targetLon = p.getLon();
//                                log.info("new Point. currLat: " + currLat + " currLon: " + currLon + " targetLat: " + targetLat + " targetLon: " + targetLon);

                                double Dy = targetLat - currLat;
                                double Dx = targetLon - currLon;

                                double VEL = 0.00001; // 1 m/s
                                double ARRIVED_THRESHOLD_METER = 1; // 1m

                                // var distance = Math.sqrt(Math.pow(Dx, 2) + Math.pow(Dy, 2));
                                // Haversine 공식
                                Function<double[], Double> haversineDistanceInMeter = (coordinates) -> {
                                    double lat1 = coordinates[0];
                                    double lon1 = coordinates[1];
                                    double lat2 = coordinates[2];
                                    double lon2 = coordinates[3];

                                    double R = 6371; // 지구 반지름 (단위: km)
                                    double dLat = Math.toRadians(lat2 - lat1);
                                    double dLon = Math.toRadians(lon2 - lon1);
                                    double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                                            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                                                    Math.sin(dLon / 2) * Math.sin(dLon / 2);
                                    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
                                    double dKm = R * c;
                                    return dKm * 1000;
                                };

                                double[] coordinates = {currLat, currLon, targetLat, targetLon};
                                var distanceInMeter = haversineDistanceInMeter.apply(coordinates);

//                                log.info("distance meter: " + distanceInMeter + ", THRESHOLD: " + ARRIVED_THRESHOLD_METER);
                                //TODO approximate. 위도에 따라 길이가 다르다. 여기선 1m 0.00001 라고 근사함.
                                while (distanceInMeter > ARRIVED_THRESHOLD_METER) {
                                    // next stream?
                                    // speed: 1m/s
                                    double theta = Math.atan2(Dy, Dx);
                                    var dy = Math.sin(theta) * VEL;
                                    var dx = Math.cos(theta) * VEL;
//                                    log.info("theta: " + theta + " dy: " + dy + " dx: " + dx);
                                    currLat += dy;
                                    currLon += dx;
                                    log.info(String.format("currLat : %f, currLon: %f", currLat, currLon));
                                    driver.setCurrLat(currLat);
                                    driver.setCurrLon(currLon);

                                    var locationDto = new LocationDto(currLat.floatValue(),
                                            currLon.floatValue(),
                                            "",
                                            "",
                                            driver.getId());

                                    // publish
                                    publishLocation(locationDto);
                                    try {
                                        Thread.sleep(100);
                                    } catch (InterruptedException e) {
                                        throw new RuntimeException(e);
                                    }

                                    targetLat = p.getLat();
                                    targetLon = p.getLon();

                                    Dy = targetLat - currLat;
                                    Dx = targetLon - currLon;

                                    coordinates = new double[]{currLat, currLon, targetLat, targetLon};
                                    distanceInMeter = haversineDistanceInMeter.apply(coordinates);
//                                    log.info("distance meter: " + distanceInMeter + ", THRESHOLD: " + ARRIVED_THRESHOLD_METER);
                                }

                            });

                            return routeResponseDto;

                        }, executor))
                .collect(Collectors.toList());

        var routeResponseDtoList = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> futures.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()));

        return routeResponseDtoList.join();
    }


    public RouteResponseDto queryRoute(double startLat, double startLon, double destLat, double destLon) {
        AtomicBoolean requestSuccess = new AtomicBoolean(true);

        var routeResponse = webClient.get().uri(uriBuilder -> uriBuilder.path(Constant.routeQueryPath)
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
                    if (error instanceof WebClientResponseException ex) {
                        System.out.println("HTTP Status Code: " + ex.getRawStatusCode());
                        System.out.println("Response Body: " + ex.getResponseBodyAsString());
                    } else {
                        System.out.println("An error occurred: " + error.getMessage());
                    }
                    System.out.println("Retrying the request...");
                })
                .retry(3) // 3 retry on error
                .block();
        return routeResponse;
    }

    public void publishLocation(LocationDto locationDto) {
//        log.info("publish location : " + locationDto);
        webClient.post().uri(uriBuilder -> uriBuilder.path(Constant.locationPublishPath).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(locationDto)
                .retrieve()
                .onStatus(HttpStatusCode::is2xxSuccessful, clientResponse -> {
//                    log.info("publishLocation req response got 200");
                    return Mono.empty();
                })
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse ->
                        Mono.error(new RuntimeException("4xx Client Error: " + clientResponse.statusCode())))
                .onStatus(HttpStatusCode::is5xxServerError, clientResponse ->
                        Mono.error(new RuntimeException("5xx Server Error: " + clientResponse.statusCode())))

                .bodyToMono(Void.class)
                .block();

    }

    private List<Double> selectRandomCoordinate(long seedId, double leftUpLat, double leftUpLon, double rightDownLat, double rightDownLon) {
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













