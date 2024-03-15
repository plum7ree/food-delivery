package com.example.route;

import com.example.route.data.dto.AddressDto;
import com.example.route.data.dto.PointDto;
import com.example.route.data.dto.RouteResponseDto;
import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.routing.util.EdgeFilter;
import com.graphhopper.util.shapes.GHPoint;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RouteApplicationTests {
    @Autowired
    private GraphHopper graphHopper;

    // dependencies:
    // 1. SpringBootTest.WebEnvironment.RANDOM_PORT
    // 2. webflux
    @Autowired
    private WebTestClient webTestClient;


    @Test
    void testRouting() {
        int nodes = graphHopper.getBaseGraph().getNodes();

        System.out.println(nodes);

        var index = graphHopper.getLocationIndex();
        System.out.println(graphHopper.getGraphHopperLocation());

        // graphhopper 한계로 인해 멀리있으면 routing 이 잘안됨.
        // 광화문 삼거리
        var startLat = 37.57524;
        var startLon = 126.97711;
        // 숭례문거리
        var destLat = 37.5606;
        var destLon = 126.9757;

        // NewYork
        // 40.7069, -74.0137
        // 40.7183,-74.0003
        // var startLat = 40.7069;
        // var startLon = -74.0137;
        // var destLat = 40.7183;
        // var destLon = -74.0003;


        var snap = index.findClosest(startLat, startLon, EdgeFilter.ALL_EDGES);
        var edge = snap.getClosestEdge();

        // toString()
        // - Edge baseNode-adjNode
        // - 121397 67849-170531
        var startNode = edge.getBaseNode();

        snap = index.findClosest(destLat, destLon, EdgeFilter.ALL_EDGES);
        edge = snap.getClosestEdge();
        var destNode = edge.getBaseNode();

        // find Path then.
        // we can do this lat, lon! we don't need base node info.
        GHPoint startPoint = new GHPoint();
        startPoint.lat = startLat;
        startPoint.lon = startLon;

        GHPoint endPoint = new GHPoint();
        endPoint.lat = destLat;
        endPoint.lon = destLon;
        // setProfile, setLocale necessary
        GHRequest req = new GHRequest(startPoint, endPoint).setProfile("car").setLocale(Locale.US);

        GHResponse res = graphHopper.route(req);

        var bestPath = res.getBest();
        var pointList = bestPath.getPoints();
        var instructionList = bestPath.getInstructions();

         System.out.println("bestPath: " + bestPath + " pointList: " + pointList + " instructionList: " + instructionList);
        // bestPath 의 데이터 형식
        // node 갯수; pointList, instruction
        // bestPath:
        //   nodes:28;
        //   (37.57524919571448,126.97710881915901),
        //   ...
        //   (37.56062757847224,126.97565445864548)
        //   [(0,,5.60561410348608,306), (-2,세종대로,9.056,709), (-8,세종대로,1167.103,85051), (-2,소공로,535.048,32103), (2,남대문로,535.784,32146), (3,세종대로,19.051707764897753,1372), (4,,0.0,0)]


    }

    @Test
    public void testRouteController() {
        // 광화문 삼거리
        var startLat = 37.57524;
        var startLon = 126.97711;
        // 숭례문거리
        var destLat = 37.5606;
        var destLon = 126.9757;


        var responseExpected = new RouteResponseDto();
        responseExpected.setPointList(List.of(new PointDto(37.57524919571448, 126.97710881915901),
                new PointDto(37.5752543, 126.9771721),
                new PointDto(37.5753357, 126.9771688),
                new PointDto(37.5753327, 126.9770668),
                new PointDto(37.5714132, 126.9771736),
                new PointDto(37.5696339, 126.9770279),
                new PointDto(37.5684059, 126.9770423),
                new PointDto(37.5671239, 126.9771089),
                new PointDto(37.5666565, 126.9771167),
                new PointDto(37.5652409, 126.9771018),
                new PointDto(37.5649238, 126.977084),
                new PointDto(37.5649179, 126.9782113),
                new PointDto(37.5648944, 126.978406),
                new PointDto(37.5648468, 126.978561),
                new PointDto(37.5644037, 126.9789674),
                new PointDto(37.5639294, 126.9794692),
                new PointDto(37.5627149, 126.980485),
                new PointDto(37.5618992, 126.9812282),
                new PointDto(37.5615617, 126.9806892),
                new PointDto(37.5614688, 126.9803692),
                new PointDto(37.5612917, 126.9794758),
                new PointDto(37.5612297, 126.9790615),
                new PointDto(37.56096, 126.97756),
                new PointDto(37.5608959, 126.9773875),
                new PointDto(37.5605411, 126.9762091),
                new PointDto(37.5605089, 126.9757927),
                new PointDto(37.5605134, 126.9754943),
                new PointDto(37.56062757847224, 126.97565445864548)));

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/route/query")
                .queryParam("startLat", startLat)
                .queryParam("startLon", startLon)
                .queryParam("destLat", destLat)
                .queryParam("destLon", destLon)
                .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(RouteResponseDto.class)
                .value(response -> {
                    assertEquals(response.getPointList(), responseExpected.getPointList());
                });
    }

}
