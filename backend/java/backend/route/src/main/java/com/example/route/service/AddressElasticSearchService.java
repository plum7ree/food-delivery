//package com.example.route.service;
//
//import com.graphhopper.GraphHopper;
//import com.graphhopper.reader.osm.OSMReader;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.EnableScheduling;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//
//@EnableScheduling
//@Service
//@Slf4j
//public class AddressElasticSearchService {
//
//
//    @Autowired
//    GraphHopper graphHopper;
//
//
////    @Scheduled(fixedDelay = 1000)
//    @PostConstruct
//    public void saveOsmIntoElasticSearch() {
//        var graph = graphHopper.getBaseGraph();
//        var reader = new OSMReader(graph, graphHopper.getOSMParsers(), graphHopper.getReaderConfig());
//        var nodeaccess = graph.getNodeAccess();
//        var details = graphHopper.getPathDetailsBuilderFactory();
//        var tags = graphHopper.getTagParserFactory();
//
//        graph.getAllEdges().
//
//        var explorer = graph.createEdgeExplorer();
//        var edges = graph.getAllEdges();
//        while(edges.next()) {
//            int baseNodeId = edges.getBaseNode(); // equal to nodeId
//            int adjacentNodeId = edges.getAdjNode(); // this is the node where this edge state is "pointing to"
//
//            log.info("{} {}", baseNodeId, adjacentNodeId);
//        }
//    }
//
//
//}
