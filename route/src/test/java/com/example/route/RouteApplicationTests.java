package com.example.route;

import com.graphhopper.GraphHopper;
import com.graphhopper.storage.RoutingCHGraph;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@RequiredArgsConstructor
class RouteApplicationTests {
    @Autowired
    GraphHopper graphHopper;

    @Test
    void contextLoads() {
        int nodes = graphHopper.getBaseGraph().getNodes();

        System.out.println(nodes);

        Map<String, RoutingCHGraph> chGraphs = graphHopper.getCHGraphs();
    }

}
